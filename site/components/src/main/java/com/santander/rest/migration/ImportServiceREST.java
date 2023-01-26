package com.santander.rest.migration;

import com.google.gson.Gson;
import com.santander.beans.Documentation;
import com.santander.rest.migration.model.*;
import org.apache.jackrabbit.util.ISO8601;
import org.apache.jackrabbit.value.BinaryValue;
import org.hippoecm.hst.content.annotations.Persistable;
import org.hippoecm.hst.content.beans.ContentNodeBindingException;
import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.workflow.WorkflowPersistenceManager;
import org.hippoecm.hst.content.beans.standard.HippoFolderBean;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import static com.santander.rest.migration.Utils.MigrationEmail.sendMail;
import static com.santander.utils.Constants.*;

public class ImportServiceREST extends org.hippoecm.hst.jaxrs.services.AbstractResource {

    private static Gson gson = new Gson();
    private static Logger logger = LoggerFactory.getLogger(ImportServiceREST.class);

    @Persistable
    @POST
    @Path("/import")
    public Response methodImport(
            @Context HttpServletRequest servletRequest,
            @FormParam("payload") String payload,
            @FormParam("imageBase64Original") String base64Original,
            @FormParam("imageBase64Thumbnail") String base64Thumbnail,
            @FormParam("mail") String mail) {

        HstRequestContext requestContext = getRequestContext(servletRequest);
        Session persistableSession = null;
        try {
             persistableSession = getPersistableSession ( requestContext );
        } catch ( RepositoryException e ) {
            e.printStackTrace ( );
        }
        WorkflowPersistenceManager wpm = null;
        DocumentationPayloadMigration documentationPayload = gson.fromJson(payload, DocumentationPayloadMigration.class);
        Base64.Decoder dec =  Base64.getUrlDecoder ();

        try {
            //sendMail(mail,"prueba inicio");

             //Original Decode Base64
            byte[] fileContentDecodeOriginal = null;
            if(Objects.nonNull (base64Original) ){
                fileContentDecodeOriginal = dec.decode ( base64Original);
            }

            //Thunmbnail Decode Base64
            byte[] fileContentDecodeThumbnail = null;
            if(Objects.nonNull (base64Thumbnail) ){
               fileContentDecodeThumbnail = dec.decode ( base64Thumbnail );
            }

            HippoFolderBean contentBaseFolder =
                    getMountContentBaseBean(requestContext);
            String productFolderPath = contentBaseFolder.getPath() + CONTENT_DOCUMENTATION_PATH + documentationPayload.getFolder ();

            //CONSULTA PARA SABER SI ESE DOCUMENTO YA EXISTE
            Query query = persistableSession.getWorkspace ( ).getQueryManager ( ).createQuery (
                    "/jcr:root" + productFolderPath + "/element(*, santanderbrxm:documentation)" +
                            "[@santanderbrxm:alias='" + documentationPayload.getAlias ( ) + "'" +
                            "and hippostd:state='unpublished']" , Query.XPATH );
            QueryResult result = query.execute ( );

            if(result.getNodes().getSize () == 0) {

                wpm = ( WorkflowPersistenceManager )
                        getPersistenceManager ( requestContext );

                String beanPathDocumentation = wpm.createAndReturn ( productFolderPath , DOC_TYPE_DOCUMENTATION , documentationPayload.getNameDocument ( ) , true );

                Node nodeGallery = persistableSession.getNode ( "/content/gallery" );
                String          uuid_icon = null;
                HippoDocBaseDTO iconPay   = new HippoDocBaseDTO ( );

                if ( Objects.nonNull ( nodeGallery ) && ( Objects.nonNull ( fileContentDecodeOriginal ) || Objects.nonNull ( fileContentDecodeThumbnail ) ) ) {
                    uuid_icon = processIcon ( nodeGallery , persistableSession , fileContentDecodeOriginal , fileContentDecodeThumbnail );
                    iconPay.setDocbase ( uuid_icon );
                }

                Documentation documentationBean = ( Documentation ) wpm.getObject ( beanPathDocumentation );
                documentationBean.setName ( documentationPayload.getNameDocument ( ) );
                documentationBean.setType ( documentationPayload.getType ( ) );
                documentationBean.setIcon ( iconPay );
                documentationBean.setTitle ( documentationPayload.getTitle ( ) );
                documentationBean.setAlias ( documentationPayload.getAlias ( ) );
                processInternalLinksDescription( documentationPayload.getDescription ( ).getNodes (), persistableSession);
                documentationBean.setDescription ( documentationPayload.getDescription ( ) );
                setOnePageDocIndex ( documentationPayload , persistableSession );
                documentationBean.setPage(documentationPayload.getPages());

                setInternalLinksNewUUID ( documentationPayload , persistableSession );
                documentationBean.setLinks ( documentationPayload.getLinks ( ) );

                wpm.update ( documentationBean );
                wpm.save ( );
                wpm.refresh ( );
                logger.info ( "guardado documentation" );
                wpm.getSession().logout();
                sendMail(mail, documentationPayload.getNameDocument(), IMPORT_CORRECTA, productFolderPath);
            }
        }
        catch (ContentNodeBindingException e) {
            sendMail(mail, documentationPayload.getNameDocument (), e.getMessage(), "");
        }
        catch ( RepositoryException | ObjectBeanManagerException e) {
            e.printStackTrace();
            sendMail(mail, documentationPayload.getNameDocument (), e.getMessage(), "");
        }

        return Response.status(201).entity("ok response").build();
    }

    private void processInternalLinksDescription ( List < HippoDocBaseDTO > nodes , Session persistableSession ) {
        //CONSULTA PARA SABER EL DOCUMENTO ENLAZADO
        if(!nodes.isEmpty ( )){
            for ( HippoDocBaseDTO node : nodes ) {
                obtainAlias(persistableSession, node);
            }
        }
    }

    private void obtainAlias ( Session persistableSession , HippoDocBaseDTO node ) {
        Query query_alias  = null;
        try {
            query_alias = persistableSession.getWorkspace ( ).getQueryManager ( ).createQuery ( "/jcr:root" + node.getPath () + "//element(*, "+node.getType ()+")[@"+SANTANDERBX_ALIAS+"='" + node.getAlias ( ) + "']" , Query.XPATH );
            if ( query_alias.execute().getNodes ( ).hasNext ( ) ) {
                Node nodeAlias = query_alias.execute().getNodes ( ).nextNode ( );
                node.setDocbase ( nodeAlias.getParent ().getIdentifier () );
                node.setAlias ( nodeAlias.getName () );
            }else {
                //enviar mail al usuario diciendo que no se ha podido encontrar el documento interno y que lo migre
                //Se seta null para que no cree el nodo vacio
                node.setAlias ( null );
                node.setAlias ( null );
            }
        } catch ( RepositoryException repositoryException ) {
            repositoryException.printStackTrace ( );
        }
    }

    private void setOnePageDocIndex ( DocumentationPayloadMigration documentationPayload , Session persistableSession ) throws RepositoryException {
        try{
            //CONSULTA PARA SABER EL DOCUMENTO ENLAZADO
            List< SectionLevelOnePayload > linksIndex = documentationPayload.getPages ( );
            if(linksIndex.size ()>0){
                for(int positionLevelOne=0;positionLevelOne<linksIndex.size ();positionLevelOne++){
                    List< DocumentationPagesPayload > linkItem =  linksIndex.get ( positionLevelOne ).getDocumentation ();
                    if(Objects.nonNull (linkItem)){
                        for(int j=0;j<linkItem.size ();j++){
                            if(Objects.nonNull ( linkItem.get ( j ).getAlias () ) && Objects.nonNull ( linkItem.get ( j ).getPath () )){
                                Query query = persistableSession.getWorkspace ( ).getQueryManager ( ).createQuery (
                                        "/jcr:root"+ linkItem.get ( j ).getPath ()+"//element(*, santanderbrxm:documentation)" +
                                                "[@santanderbrxm:alias='"+ linkItem.get ( j ).getAlias ()+"'" +
                                                "and hippostd:state='unpublished']", Query.XPATH );

                                QueryResult result = query.execute ( );

                                if(result.getNodes().getSize () == 1) {
                                    for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                                        Node docPageOneIndex = nodeIterator.nextNode ( );
                                        linksIndex.get ( positionLevelOne ).getDocumentation ().get (j).setDocbase ( docPageOneIndex.getIdentifier () );
                                        processInternalLinksDescription( linksIndex.get ( positionLevelOne ).getDescription ().getNodes (), persistableSession);
                                    }
                                }
                            }
                        }
                    }
                    setSecondPageDocIndex ( linksIndex, persistableSession, positionLevelOne );
                }
            }
        }
        catch ( RepositoryException e){
            logger.error("PathNotFoundException processSectionOne documentation dont exist");
        }

    }

    private void setSecondPageDocIndex ( List < SectionLevelOnePayload > links , Session persistableSession , int positionLevelOne ) throws RepositoryException {
        try{
        //CONSULTA PARA SABER EL DOCUMENTO ENLAZADO
        List< SectionLevelTwoPayload> linksIndex = links.get ( positionLevelOne ).getSections ();
        if(linksIndex.size ()>0){
            for(int positionLevelTwo=0;positionLevelTwo<linksIndex.size ();positionLevelTwo++){
                List< DocumentationPagesPayload > linkItem =  linksIndex.get ( positionLevelTwo ).getDocumentation ();
                if(Objects.nonNull (linkItem)){
                    for(int j=0;j<linkItem.size ();j++){
                        if(Objects.nonNull ( linkItem.get ( j ).getAlias () ) && Objects.nonNull ( linkItem.get ( j ).getPath () )){
                            Query query = persistableSession.getWorkspace ( ).getQueryManager ( ).createQuery (
                                    "/jcr:root"+ linkItem.get ( j ).getPath ()+"//element(*, santanderbrxm:documentation)" +
                                            "[@santanderbrxm:alias='"+ linkItem.get ( j ).getAlias ()+"'" +
                                            "and hippostd:state='unpublished']", Query.XPATH );

                            QueryResult result = query.execute ( );

                            if(result.getNodes().getSize () == 1) {
                                for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                                    Node docPageOneIndex = nodeIterator.nextNode ( );
                                    linksIndex.get ( positionLevelTwo ).getDocumentation ().get (j).setDocbase ( docPageOneIndex.getIdentifier () );
                                    processInternalLinksDescription( linksIndex.get ( positionLevelTwo ).getDescription ().getNodes (), persistableSession);
                                }
                            }
                        }
                    }
                }
                setThreePageDocIndex(linksIndex, persistableSession, positionLevelTwo);

            }
        }
        }
        catch ( RepositoryException e){
            logger.error("PathNotFoundException processSectionOne documentation dont exist");
        }
    }

    private void setThreePageDocIndex ( List< SectionLevelTwoPayload> links , Session persistableSession , int positionLevelTwo ) {
        try{
            //CONSULTA PARA SABER EL DOCUMENTO ENLAZADO
            List< SectionLevelThreePayload> linksIndex = links.get ( positionLevelTwo ).getSections ();
            if(linksIndex.size ()>0){
                for(int positionLevelThree=0;positionLevelThree<linksIndex.size ();positionLevelThree++){
                    List< DocumentationPagesPayload > linkItem =  linksIndex.get ( positionLevelThree ).getDocumentation ();
                    if(Objects.nonNull (linkItem)){
                        for(int j=0;j<linkItem.size ();j++){
                            if(Objects.nonNull ( linkItem.get ( j ).getAlias () ) && Objects.nonNull ( linkItem.get ( j ).getPath () )){
                                Query query = persistableSession.getWorkspace ( ).getQueryManager ( ).createQuery (
                                        "/jcr:root"+ linkItem.get ( j ).getPath ()+"//element(*, santanderbrxm:documentation)" +
                                                "[@santanderbrxm:alias='"+ linkItem.get ( j ).getAlias ()+"'" +
                                                "and hippostd:state='unpublished']", Query.XPATH );

                                QueryResult result = query.execute ( );

                                if(result.getNodes().getSize () == 1) {
                                    for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                                        Node docPageOneIndex = nodeIterator.nextNode ( );
                                        linksIndex.get ( positionLevelThree ).getDocumentation ().get (j).setDocbase ( docPageOneIndex.getIdentifier () );
                                        processInternalLinksDescription( linksIndex.get ( positionLevelThree ).getDescription ().getNodes (), persistableSession);
                                    }
                                }
                            }
                        }
                    }
                    setFourPageDocIndex(linksIndex, persistableSession, positionLevelThree);

                }
            }
        }
        catch ( RepositoryException e){
            logger.error("PathNotFoundException processSectionOne documentation dont exist");
        }
    }

    private void setFourPageDocIndex ( List< SectionLevelThreePayload> links , Session persistableSession , int positionLevelThree ) {
        try{
            //CONSULTA PARA SABER EL DOCUMENTO ENLAZADO
            List< SectionLevelFourPayload> linksIndex = links.get ( positionLevelThree ).getSections ();
            if(linksIndex.size ()>0){
                for(int positionLevelFour=0;positionLevelFour<linksIndex.size ();positionLevelFour++){
                    List< DocumentationPagesPayload > linkItem =  linksIndex.get ( positionLevelFour ).getDocumentation ();
                    if(Objects.nonNull (linkItem)){
                        for(int j=0;j<linkItem.size ();j++){
                            if(Objects.nonNull ( linkItem.get ( j ).getAlias () ) && Objects.nonNull ( linkItem.get ( j ).getPath () )){
                                Query query = persistableSession.getWorkspace ( ).getQueryManager ( ).createQuery (
                                        "/jcr:root"+ linkItem.get ( j ).getPath ()+"//element(*, santanderbrxm:documentation)" +
                                                "[@santanderbrxm:alias='"+ linkItem.get ( j ).getAlias ()+"'" +
                                                "and hippostd:state='unpublished']", Query.XPATH );

                                QueryResult result = query.execute ( );

                                if(result.getNodes().getSize () == 1) {
                                    for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                                        Node docPageOneIndex = nodeIterator.nextNode ( );
                                        linksIndex.get ( positionLevelFour ).getDocumentation ().get (j).setDocbase ( docPageOneIndex.getIdentifier () );
                                        processInternalLinksDescription( linksIndex.get ( positionLevelFour ).getDescription ().getNodes (), persistableSession);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch ( RepositoryException e){
            logger.error("PathNotFoundException processSectionOne documentation dont exist");
        }
    }

    private void setInternalLinksNewUUID ( DocumentationPayloadMigration documentationPayload , Session persistableSession ) throws RepositoryException {
        //CONSULTA PARA SABER EL DOCUMENTO ENLAZADO
        List < LinksPayloadMigration > linksIndex = documentationPayload.getLinks ( );
        if(linksIndex.size ()>0){
            for(int i=0;i<linksIndex.size ();i++){
                LinksPayloadMigration linkItem =  linksIndex.get ( i );

                Query query = persistableSession.getWorkspace ( ).getQueryManager ( ).createQuery (
                        "/jcr:root"+linkItem.getInternalLink ().getPath ()+"//element(*, santanderbrxm:documentation)" +
                                "[@santanderbrxm:alias='"+ linkItem.getInternalLink ().getAlias ()+"'" +
                                "and hippostd:state='unpublished']", Query.XPATH );

                QueryResult result = query.execute ( );

                if(result.getNodes().getSize () == 1) {
                    for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                        Node docPageOneIndex = nodeIterator.nextNode ( );
                        linkItem.getInternalLink ().setDocbase ( docPageOneIndex.getIdentifier () );
                    }
                }
            }
        }
    }

    private String processIcon ( Node nodeGallery , Session persistableSession , byte[] fileContentDecodeOriginal , byte[] fileContentDecodeThumbnail ) throws RepositoryException {
        Node nodeImage = null;
        try {

            nodeImage = nodeGallery.addNode ( "prueba", "hippo:handle" ).addNode (  "prueba", "hippogallery:imageset" );
            nodeImage.setProperty ( "hippogallery:filename" , "prueba.PNG" );

            //Original
            Node nodeOriginal = nodeImage.addNode ( "hippogallery:original" , "hippogallery:image");
            nodeOriginal.setProperty ( "jcr:mimeType", "image/png" );
            nodeOriginal.setProperty ( "jcr:lastModified" ,  ISO8601.format ( Calendar.getInstance ( ) ) );
            nodeOriginal.setProperty ( "hippogallery:width" , "47" );
            nodeOriginal.setProperty ( "hippogallery:height" , "49" );

            //Binary binaryValeOriginal = persistableSession.getValueFactory().createBinary(new ByteArrayInputStream (fileContentDecodeOriginal));
            BinaryValue binaryValeOriginal = new BinaryValue ( fileContentDecodeOriginal );
            nodeOriginal.setProperty ( "jcr:data" , binaryValeOriginal );

            //Thumbnail
            Node nodeThumbnail = nodeImage.getNode ( "hippogallery:thumbnail");
            nodeThumbnail.setProperty ( "jcr:mimeType", "image/png" );
            nodeThumbnail.setProperty ( "jcr:lastModified" ,  ISO8601.format ( Calendar.getInstance ( ) ) );
            nodeThumbnail.setProperty ( "hippogallery:width" , "47" );
            nodeThumbnail.setProperty ( "hippogallery:height" , "49" );

            //Binary binaryValueThumbnail = persistableSession.getValueFactory().createBinary(new ByteArrayInputStream (fileContentDecodeThumbnail));
            BinaryValue binaryValueThumbnail = new BinaryValue ( fileContentDecodeThumbnail );
            nodeThumbnail.setProperty ( "jcr:data" , binaryValueThumbnail );

            persistableSession.save ();


        } catch ( RepositoryException e ) {
            e.printStackTrace ( );
        }
        return nodeImage != null ? nodeImage.getParent ( ).getIdentifier ( ) : null;
    }
}
