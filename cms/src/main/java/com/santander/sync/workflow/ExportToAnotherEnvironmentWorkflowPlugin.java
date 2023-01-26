package com.santander.sync.workflow;

import com.google.gson.Gson;
import com.santander.sync.workflow.APIRestFull.Controller.GitHubService;
import com.santander.sync.workflow.model.*;
import com.santander.utils.Constants;
import com.santander.utils.GlobalConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.wicket.model.StringResourceModel;
import org.hippoecm.addon.workflow.StdWorkflow;
import org.hippoecm.addon.workflow.WorkflowDescriptorModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.hippoecm.hst.container.RequestContextProvider;
import org.hippoecm.repository.api.WorkflowDescriptor;
import org.hippoecm.repository.impl.SessionDecorator;
import org.onehippo.repository.documentworkflow.DocumentWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;

import javax.jcr.*;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import static com.santander.utils.Constants.*;

@Component
@EnableAutoConfiguration
public class ExportToAnotherEnvironmentWorkflowPlugin extends RenderPlugin < WorkflowDescriptor > implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger ( ExportToAnotherEnvironmentWorkflowPlugin.class );
    private static final Gson gson = new Gson ( );
    private final transient GlobalConfiguration globalConfiguration = new GlobalConfiguration ( );

    @Autowired
    private GitHubService gitHubService;

    public ExportToAnotherEnvironmentWorkflowPlugin ( IPluginContext context , IPluginConfig config ) {
        super ( context , config );

        LOGGER.info ( "Rendering Migrate Option" );


        add ( new StdWorkflow < DocumentWorkflow > ( "Procesing" ,
                new StringResourceModel ( "Procesing" , this ) ,
                ( WorkflowDescriptorModel ) getModel ( ) ) {


            @Override
            public String getSubMenu ( ) {
                // TODO Auto-generated method stub
                return new StringResourceModel ( "MigrateMenu" , this ).getString ( );
            }

            @Override
            protected String execute ( DocumentWorkflow workflow ) throws Exception {
                LOGGER.info ( "Executing Migration for object {}" , workflow.getNode ( ).getName ( ) );
                //workflow.add("new-document", "myproject:index", "index");

                return sendDocument ( workflow.getNode ( ) );
            }

        } );

    }

    public static String rtrnFldr ( String doc , String path ) {
        String[] str = path.substring ( path.indexOf ( doc ) + doc.length ( ) ).split ( "/" );
        String pathDoc = "";
        for ( int i = 0 ; i <= ( str.length - 2 ) ; i++ ) {
            if ( i != ( str.length - 2 ) ) {
                pathDoc = pathDoc + str[ i ] + "/";
            } else {
                pathDoc = pathDoc + str[ i ];
            }
        }
        return pathDoc;
    }

    public String sendDocument ( Node node ) {

        //Session
        //Session threadSafeSession = null;
        //String mail = null;

        try {
            Session threadSafeSession = RequestContextProvider.get ( ).getSession ( ).impersonate ( new SimpleCredentials ( RequestContextProvider.get ( ).getSession ( ).getUserID ( ) , new char[ 0 ] ) );
            String mail = ( ( SessionDecorator ) threadSafeSession ).getUser ( ).getEmail ( );


            String importUrl = globalConfiguration.getConfigurationById ( IMPORT_TARGET_URL , threadSafeSession );

            /**Creating object of Documentation **/
            DocumentationPayload documentationPayload = new DocumentationPayload ( );
            ImageBase64 imageBase64 = new ImageBase64 ();

            String type = null;
            try {
                type = node.getNode ( node.getName ( ) ).getPrimaryNodeType ( ).getName ( );
            } catch ( RepositoryException e ) {
                e.printStackTrace ( );

            }
            LOGGER.info ( "the type is {} of Object documentationPayloadMigration " , type );
            String json = "";

            if ( Constants.SANTANDERBRXM_DOCUMENTATION.equals ( type ) ) {

                documentationPayload = getObjectDataApiContainer ( documentationPayload , imageBase64, node , threadSafeSession );
                json = gson.toJson ( documentationPayload );
                LOGGER.info ( "Json represenatation of Object documentationPayloadMigration is  {}" , json );

                List < NameValuePair > urlParameters = new ArrayList <> ( );
                urlParameters.add ( new BasicNameValuePair ( "payload" , json ) );

                Base64.Encoder enc =  Base64.getUrlEncoder ().withoutPadding ();

                if(Objects.nonNull ( imageBase64.getBase64Original () )){
                    urlParameters.add ( new BasicNameValuePair ( "imageBase64Original" ,   enc.encodeToString ( imageBase64.getBase64Original () ) ) );
                }
                if(Objects.nonNull ( imageBase64.getBase64Thumbnail () )){
                    urlParameters.add ( new BasicNameValuePair ( "imageBase64Thumbnail" , enc.encodeToString ( imageBase64.getBase64Thumbnail ( ) ) ) );
                }

                urlParameters.add ( new BasicNameValuePair ( "mail" , mail ) );

                LOGGER.info ( "CONECTAMOS A GIT" );
                /**  GitControl gitControl = new GitControl ( "C:\\Users\\x433106\\OneDrive - Santander Office 365\\Escritorio\\prueba" ,
                                                            "https://github.com/pagonxt/brx-cms.git" );
                   gitControl.cloneRepo ();**/


             //  GitHubService git = new GitHubService (  );
             //  git.getGitHubContent (  "ss", "pagonxt", "devexp.apigee-integration-abstraction-layers", "yamls/MngmentDocBrxm/ManagementDocumentBloomreach.yaml", "develop" );


                gitHubService.getGitHubContent("pagonxt",
                                               "devexp.apigee-integration-abstraction-layers", "yamls/MngmentDocBrxm/ManagementDocumentBloomreach.yaml", "develop");





                /**
                HttpPost post = new HttpPost ( importUrl );

                try {
                    post.setEntity ( new UrlEncodedFormEntity ( urlParameters ) );
                } catch ( UnsupportedEncodingException e ) {
                    LOGGER.info ( "Error UrlEncodedFormEntity {}" , e.getMessage ( ) );
                    e.printStackTrace ( );

                }

                post.setHeader ( "Accept" , MediaType.APPLICATION_JSON );
                post.setHeader ( "Content-type" , MediaType.APPLICATION_FORM_URLENCODED );

                LOGGER.info ( "Before post" );
                try ( CloseableHttpClient httpClient = HttpClients.createDefault ( ) ;
                      CloseableHttpResponse response = httpClient.execute ( post ) ) {
                } catch ( Exception e ) {
                    LOGGER.info ( "Error CloseableHttpClient {}" , e.getMessage ( ) );

                }**/


                try {
                    threadSafeSession.logout ( );
                } catch ( Exception e ) {
                    LOGGER.info ( "Error logout session {}" , e.getMessage ( ) );
                    LOGGER.info ( "Error logout session {}" , e.getMessage ( ) );
                    e.printStackTrace ( );
                }
            }
        } catch ( ItemNotFoundException e ) {
            LOGGER.info ( "Error obtain mail {}" , e.getMessage ( ) );
            e.printStackTrace ( );
        } catch ( RepositoryException e ) {
            LOGGER.info ( "Error obtain session {}" , e.getMessage ( ) );
            e.printStackTrace ( );

        }
        return null;
    }

    public DocumentationPayload getObjectDataApiContainer ( DocumentationPayload documentationPayload , ImageBase64 imageBase64 , Node node , Session threadSafeSession ) {
        try {
            /**insert the data**/

            //Before Documentation folder
            String fldr_doc = rtrnFldr ( DOC , node.getPath ( ) );
            documentationPayload.setFolder ( fldr_doc );

            String namePropertieNode = node.getName ( );
            documentationPayload.setNamePropertieNode ( namePropertieNode );

            try {
                String nameDocument = node.getProperty ( "hippo:name" ).getString ( );
                documentationPayload.setNameDocument ( nameDocument );
            } catch ( PathNotFoundException e ) {
                LOGGER.error ( " error obtain name documentation migration {}" , e.getMessage ( ) );
                documentationPayload.setNameDocument ( namePropertieNode );

            }

            node = node.getNode ( node.getName ( ) );

            try {
                //TYPE
                documentationPayload.setType ( node.getProperty ( SANTANDERBRXM_TYPE ).getString ( ) );
            } catch ( PathNotFoundException e ) {
                LOGGER.error ( " error obtain TYPE documentation migration {}" , e.getMessage ( ) );
            }

            //ICON
            HippoDocBaseDTO hippoDocBaseDTO = processIcon ( node , imageBase64, threadSafeSession );
            documentationPayload.setIcon ( hippoDocBaseDTO );

            try {
                //TITLE
                documentationPayload.setTitle ( node.getProperty ( SANTANDERBRXM_TITLE ).getString ( ) );
            } catch ( PathNotFoundException e ) {
                LOGGER.error ( " error obtain TYPE documentation migration {}" , e.getMessage ( ) );
            }

            try {
                //ALIAS
                documentationPayload.setAlias ( node.getProperty ( SANTANDERBX_ALIAS ).getString ( ) );
            } catch ( PathNotFoundException e ) {
                LOGGER.error ( " error obtain TYPE documentation migration {}" , e.getMessage ( ) );
            }

            //Description
            DescriptionPayload desciptionPayload = processDescription ( node, threadSafeSession );
            documentationPayload.setDescription ( desciptionPayload );

            //PAGES
            List < SectionLevelOnePayload > sectionOnePayloadList = processPages ( documentationPayload , node, threadSafeSession );
            documentationPayload.setPages ( sectionOnePayloadList );

            //LINKS
            List < LinksPayloadMigration > listLinks = processLinks ( documentationPayload , node, threadSafeSession );
            documentationPayload.setLinks ( listLinks );

        } catch ( Exception e ) {
            LOGGER.info ( "Fail to set ApiContainerPayload Export Migration {}" , e.getMessage ( ) );
        }

        return documentationPayload;
    }

    private
    HippoDocBaseDTO processIcon ( Node node , ImageBase64 imageBase64 , Session threadSafeSession ) {
        HippoDocBaseDTO hippoDocBaseDTO = new HippoDocBaseDTO ( );
        try {
            Node nodeIcon = node.getNode ( Constants.SANTANDERBRXM_ICON );
            if( !nodeIcon.getProperty ( Constants.HIPPO_DOCBASE ).getString ( ).equals ( "cafebabe-cafe-babe-cafe-babecafebabe" ) && !nodeIcon.getProperty ( Constants.HIPPO_DOCBASE ).getString ( ).isEmpty ()) {
                hippoDocBaseDTO.setDocbase ( nodeIcon.getProperty ( Constants.HIPPO_DOCBASE ).getString ( ) );
                QueryResult queryResult = obtainResult ( threadSafeSession , nodeIcon );
                for ( NodeIterator nodeParentIterator = queryResult.getNodes ( ) ; nodeParentIterator.hasNext ( ) ; ) {
                    Node nodeParent = nodeParentIterator.nextNode ( );
                    for ( NodeIterator nodeImageIter = nodeParent.getNodes ( ) ; nodeImageIter.hasNext ( ) ; ) {
                        Node nodeImage = nodeImageIter.nextNode ( );
                        // String imageURL = obtainURLImage ( nodeImage );
                        // exportContent(threadSafeSession, imageURL, nodeImage);
                        // importContent ( threadSafeSession , imageURL , nodeImage );
                        imageBase64.setBase64Original (encodeImageOriginal ( nodeImage ) );
                        imageBase64.setBase64Thumbnail (encodeImageThumbnail ( nodeImage ));
                    }
                }
            }
        } catch ( PathNotFoundException e ) {
            LOGGER.error ( "PathNotFoundException icon dont exist" );

        } catch ( RepositoryException e ) {
            LOGGER.error ( " RepositoryException icon dont exist" );
        } catch ( IOException e ) {
            e.printStackTrace ( );
        }

        return hippoDocBaseDTO;
    }

    private DescriptionPayload processDescription ( Node node , Session threadSafeSession ) {
        DescriptionPayload descriptionPayload = new DescriptionPayload ( );
        try {
            Node nodeDescription = node.getNode ( Constants.SANTANDERBRXM_DESCRIPTION );
            descriptionPayload.setContent ( nodeDescription.getProperty ( Constants.HIPPO_CONTENT ).getString ( ) );

            //Si tiene subNodos tiene enlaces internos en la descripcion, los recorremos y los guardamos para exportar
            processInternalLinkDescription(nodeDescription,threadSafeSession,descriptionPayload);

        } catch ( PathNotFoundException e ) {
            LOGGER.error ( "PathNotFoundException description dont exist" );

        } catch ( RepositoryException e ) {
            LOGGER.error ( " RepositoryException nodo description dont exist" );

        }
        return descriptionPayload;
    }

    private
    void processInternalLinkDescription ( Node nodeDescription , Session threadSafeSession , DescriptionPayload descriptionPayload ) {
        try{
        if(Objects.nonNull ( nodeDescription.getNodes ( ) )){
            List<HippoDocBaseDTO> internalLinksDtoList = new ArrayList <> ( );
            for ( NodeIterator nodeDescrIter = nodeDescription.getNodes ( ) ; nodeDescrIter.hasNext ( ) ; ) {
                HippoDocBaseDTO  internalLinksDto = new HippoDocBaseDTO ();
                Node nodeLinkInternal = nodeDescrIter.nextNode ();
                //internalLinksDto.setDocbase (processPathDocBase(nodeLinkInternal.getProperty ( "hippo:docbase" ).getString (),threadSafeSession));
                internalLinksDto.setAlias( processAlias ( nodeLinkInternal.getProperty ( "hippo:docbase" ).getString ( ), threadSafeSession ) );
                internalLinksDto.setPath ( processPathDocBase(nodeLinkInternal.getProperty ( "hippo:docbase" ).getString (),threadSafeSession));
                internalLinksDto.setType ( processType(nodeLinkInternal.getProperty ( "hippo:docbase" ).getString (),threadSafeSession) );
                internalLinksDtoList.add ( internalLinksDto );
            }
            descriptionPayload.setNodes ( internalLinksDtoList );
        }
        } catch ( PathNotFoundException e ) { LOGGER.error ( "PathNotFoundException description dont exist" );
        } catch ( RepositoryException e ) { LOGGER.error ( " RepositoryException nodo description dont exist" ); }
    }

    private
    String processType ( String uuid_doc , Session threadSafeSession ) {
        String type = "";
        try {
            Query query = threadSafeSession.getWorkspace().getQueryManager().createQuery( QUERY_DOC+uuid_doc+"']", Query.XPATH);
            if( query.execute().getNodes ( ).hasNext ( ) ){
                Node node = query.execute().getNodes ( ).nextNode ( );
                for(NodeIterator nodeStates = node.getNodes (); nodeStates.hasNext ();){
                    Node nodeState = nodeStates.nextNode ();
                    type =  nodeState.getPrimaryNodeType ().getName ();
                    if(!type.isEmpty ()){
                        break;
                    }
                }
            }
        } catch ( RepositoryException repositoryException ) {
            repositoryException.printStackTrace ( );
        }
        return type;
    }

    private
    String processAlias ( String uuid_doc, Session threadSafeSession ) {
        String alias = "";
        try {
            Query query = threadSafeSession.getWorkspace().getQueryManager().createQuery( QUERY_DOC+uuid_doc+"']", Query.XPATH);
            if( query.execute().getNodes ( ).hasNext ( ) ){
                Node node = query.execute().getNodes ( ).nextNode ( );
                for(NodeIterator nodeStates = node.getNodes (); nodeStates.hasNext ();){
                    Node nodeState = nodeStates.nextNode ();
                    alias =  nodeState.getProperty ( "santanderbrxm:alias" ).getString ();
                    if(!alias.isEmpty ()){
                        break;
                    }
                }
            }
        } catch ( RepositoryException repositoryException ) {
            repositoryException.printStackTrace ( );
        }
        return alias;
    }

    private
    String processNameContent ( String uuid_doc , Session threadSafeSession ) {
        String name=null;
        try{
            Query query = threadSafeSession.getWorkspace().getQueryManager().createQuery( QUERY_DOC+uuid_doc+"']", Query.XPATH);
            if( query.execute().getNodes ( ).hasNext ( ) ){
                Node node = query.execute().getNodes ( ).nextNode ( );
                name =  node.getProperty ( "hippo:name" ).getString ();
            }
       } catch ( PathNotFoundException e ) {
            LOGGER.error ( "PathNotFoundException description dont exist" );

        } catch ( RepositoryException e ) {
            LOGGER.error ( " RepositoryException nodo descri9ption dont exist" );

        }
        return  name;
    }

    private String processPathDocBase ( String uuid_doc , Session threadSafeSession ) {
        String path=null;
        try{
            Query query = threadSafeSession.getWorkspace().getQueryManager().createQuery( QUERY_DOC+uuid_doc+"']", Query.XPATH);
            QueryResult result = query.execute();
            for ( NodeIterator nodeParentIterator = result.getNodes ( ) ; nodeParentIterator.hasNext ( ) ; ) {
                   path = nodeParentIterator.nextNode ( ).getParent ().getPath ();
            }

        } catch ( PathNotFoundException e ) {
            LOGGER.error ( "PathNotFoundException description dont exist" );

        } catch ( RepositoryException e ) {
            LOGGER.error ( " RepositoryException nodo description dont exist" );

        }
        return  path;
    }

    private List < LinksPayloadMigration > processLinks ( DocumentationPayload documentationPayload , Node node , Session threadSafeSession ) {
        List < LinksPayloadMigration > listLinks = new ArrayList < LinksPayloadMigration > ( );
        try {
            Node nodeLinks = node.getNode ( Constants.SANTANDERBRXM_LINKS );
            if ( nodeLinks != null ) {
                NodeIterator linkIterator = node.getNodes ( Constants.SANTANDERBRXM_LINKS );
                while ( linkIterator.hasNext ( ) ) {
                    LinksPayloadMigration linksPayload = new LinksPayloadMigration ( );
                    Node nodeLink = linkIterator.nextNode ( );
                    linksPayload.setExternalLink ( nodeLink.getProperty ( SANTANDERBRXM_EXTERNAL_LINK ).getString ( ) );
                    linksPayload.setTab ( nodeLink.getProperty ( TAB ).getString ( ) );
                    linksPayload.setTitle ( nodeLink.getProperty ( SANTANDERBRXM_TITLE ).getString ( ) );

                    //internalLink
                    InternalLinkMigration internalLinkMigration = new InternalLinkMigration ( );
                    Node nodeInternalLink = nodeLink.getNode ( INTERNAL_LINK );
                    String uuid_doc = nodeInternalLink.getProperty (HIPPO_DOCBASE ).getString ( );
                    Query query = threadSafeSession.getWorkspace().getQueryManager().createQuery( QUERY_DOC+uuid_doc+"']", Query.XPATH);
                    QueryResult result = query.execute();

                    if(result.getNodes().getSize () == 1) {
                        for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                            Node nodeOriginalDocIndx = null;
                            nodeOriginalDocIndx = nodeIterator.nextNode ( );
                            Query queryDocIndex = threadSafeSession.getWorkspace().getQueryManager().createQuery(
                                    QUERY_ROOT+nodeOriginalDocIndx.getPath ()+QUERY_DOC_PUBLISH, Query.XPATH);
                            QueryResult resultInternal = queryDocIndex.execute();
                            if(resultInternal.getNodes().getSize () == 1) {
                                for ( NodeIterator nodeIterator2 = resultInternal.getNodes ( ) ; nodeIterator2.hasNext ( ) ; ) {
                                    Node docItem = null;
                                    String alias = "";
                                    docItem = nodeIterator2.nextNode ( );
                                    alias =  docItem.getProperty (SANTANDERBX_ALIAS).getString ();
                                    internalLinkMigration.setAlias ( alias );
                                    internalLinkMigration.setPath ( docItem.getParent ().getPath () );
                                    //COMENTAMOS EL SETEO DE DOCBASE PORQUE YA SE SETE EN LA RECEPCIOIN CON LA BSUQYEDA POR ALIAS PARA INCORPORAR EL NUEVO UIID DEL LADO RECEPCION
                                    //internalLinkMigration.setDocbase ( nodeInternalLink.getProperty ( Constants.HIPPO_DOCBASE ).getString ( ) );
                                }
                            }
                            sendDocument ( nodeOriginalDocIndx )   ;
                        }
                        linksPayload.setInternalLink ( internalLinkMigration );
                        listLinks.add ( linksPayload );
                    }
                }
            }
            documentationPayload.setLinks ( listLinks );
        } catch ( PathNotFoundException path ) {
            LOGGER.error ( "PathNotFoundException processLinks dont exist" );
        } catch ( RepositoryException e ) {
            LOGGER.error ( " RepositoryException  processLinks dont exist" );
        }

        return listLinks;
    }

    private List < SectionLevelOnePayload > processPages ( DocumentationPayload documentationPayload , Node node , Session threadSafeSession ) {

        List < SectionLevelOnePayload > sectionOnePayloadList = new ArrayList < SectionLevelOnePayload > ( );
        try {
            NodeIterator nodeSectionOneIterator = node.getNodes ( SANTANDERBRXM_PAGE );
            while ( nodeSectionOneIterator.hasNext ( ) ) {

                SectionLevelOnePayload sectionOnePayload = new SectionLevelOnePayload ( );

                Node nodeSectionOne = nodeSectionOneIterator.nextNode ( );

                sectionOnePayload.setTitle ( nodeSectionOne.getProperty ( SANTANDERBRXM_TITLE ).getString ( ) );

                //Description level One
                Node nodeDescription = nodeSectionOne.getNode ( Constants.SANTANDERBRXM_DESCRIPTION );
                DescriptionPayload descriptionPayload = new DescriptionPayload ( );
                descriptionPayload.setContent ( nodeDescription.getProperty ( Constants.HIPPO_CONTENT ).getString ( ) );
                sectionOnePayload.setDescription ( descriptionPayload );

                //Si tiene subNodos tiene enlaces internos en la descripcion, los recorremos y los guardamos para exportar
                processInternalLinkDescription(nodeDescription,threadSafeSession,descriptionPayload);

                //Section Level Two
                 processSecond ( nodeSectionOne , sectionOnePayload, threadSafeSession );

                //Documentation Level One
                List < DocumentationPagesPayload > documentationPagesPayloadList = new ArrayList < DocumentationPagesPayload > ( );
                NodeIterator nodeDocumentationIterator = nodeSectionOne.getNodes ( SANTANDERBRXM_DOCUMENTATION );
                while ( nodeDocumentationIterator.hasNext ( ) ) {
                    Node nodeDocumentation = nodeDocumentationIterator.nextNode ( );
                    DocumentationPagesPayload docuemntationPayload = new DocumentationPagesPayload ( );
                    String uuid_doc = nodeDocumentation.getProperty (HIPPO_DOCBASE ).getString ( );
                    Query query = threadSafeSession.getWorkspace().getQueryManager().createQuery( QUERY_DOC+uuid_doc+"']", Query.XPATH);
                    QueryResult result = query.execute();

                    if(result.getNodes().getSize () == 1) {
                        for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                            Node nodeOriginalDocIndx = null;
                            nodeOriginalDocIndx = nodeIterator.nextNode ( );
                            Query queryDocIndex = threadSafeSession.getWorkspace().getQueryManager().createQuery(
                                    QUERY_ROOT+nodeOriginalDocIndx.getPath ()+QUERY_DOC_PUBLISH, Query.XPATH);
                            QueryResult resultInternal = queryDocIndex.execute();
                            if(resultInternal.getNodes().getSize () == 1) {
                                for ( NodeIterator nodeIterator2 = resultInternal.getNodes ( ) ; nodeIterator2.hasNext ( ) ; ) {
                                    Node docItem = null;
                                    String alias = "";
                                    docItem = nodeIterator2.nextNode ( );
                                    alias =  docItem.getProperty (SANTANDERBX_ALIAS).getString ();
                                    docuemntationPayload.setAlias ( alias );
                                    docuemntationPayload.setPath ( docItem.getParent ().getPath () );
                                }
                            }
                            sendDocument ( nodeOriginalDocIndx )   ;
                            documentationPagesPayloadList.add ( docuemntationPayload );
                        }
                        sectionOnePayload.setDocumentation ( documentationPagesPayloadList );

                    }
                }
                sectionOnePayloadList.add ( sectionOnePayload );
            }

        } catch ( PathNotFoundException e ) {
            LOGGER.error ( "PathNotFoundException processPages dont exist" );

        } catch ( RepositoryException e ) {
            LOGGER.error ( " RepositoryException  processPages dont exist" );
        }
        return sectionOnePayloadList;
    }

    private void processSecond ( Node nodeSectionOne , SectionLevelOnePayload sectionOnePayload , Session threadSafeSession ) {
        List < SectionLevelTwoPayload > sectionLevelTwoPayloadlist = new ArrayList < SectionLevelTwoPayload > ( );
        try {
            NodeIterator nodeSectionTwoIterator = nodeSectionOne.getNodes ( SANTANDERBRXM_SECTIONS );
            while ( nodeSectionTwoIterator.hasNext ( ) ) {

                SectionLevelTwoPayload sectionLevelTwoPayload = new SectionLevelTwoPayload ( );

                Node nodeSectionTwo = nodeSectionTwoIterator.nextNode ( );

                sectionLevelTwoPayload.setTitle ( nodeSectionTwo.getProperty ( SANTANDERBRXM_TITLE ).getString ( ) );

                //Description Level two
                Node nodeDescriptionTwoLevel = nodeSectionTwo.getNode ( Constants.SANTANDERBRXM_DESCRIPTION );
                DescriptionPayload descriptionPayloadLevelTwo = new DescriptionPayload ( );
                descriptionPayloadLevelTwo.setContent ( nodeDescriptionTwoLevel.getProperty ( Constants.HIPPO_CONTENT ).getString ( ) );
                sectionLevelTwoPayload.setDescription ( descriptionPayloadLevelTwo );

                //Si tiene subNodos tiene enlaces internos en la descripcion, los recorremos y los guardamos para exportar
                processInternalLinkDescription(nodeDescriptionTwoLevel,threadSafeSession,descriptionPayloadLevelTwo);

                //Section Level Three
                processThree ( nodeSectionTwo , sectionLevelTwoPayload, threadSafeSession );

                //Documentation Level Two
                List < DocumentationPagesPayload > documentationPagesPayloadList = new ArrayList < DocumentationPagesPayload > ( );
                NodeIterator nodeDocumentationIterator = nodeSectionTwo.getNodes ( SANTANDERBRXM_DOCUMENTATION );
                while ( nodeDocumentationIterator.hasNext ( ) ) {
                    Node nodeDocumentation = nodeDocumentationIterator.nextNode ( );
                    DocumentationPagesPayload docuemntationPayload = new DocumentationPagesPayload ( );
                    String uuid_doc = nodeDocumentation.getProperty (HIPPO_DOCBASE ).getString ( );
                    Query query = threadSafeSession.getWorkspace().getQueryManager().createQuery( QUERY_DOC+uuid_doc+"']", Query.XPATH);
                    QueryResult result = query.execute();

                    if(result.getNodes().getSize () == 1) {
                        for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                            Node nodeOriginalDocIndx = null;
                            nodeOriginalDocIndx = nodeIterator.nextNode ( );
                            Query queryDocIndex = threadSafeSession.getWorkspace().getQueryManager().createQuery(
                                    QUERY_ROOT+nodeOriginalDocIndx.getPath ()+QUERY_DOC_PUBLISH, Query.XPATH);
                            QueryResult resultInternal = queryDocIndex.execute();
                            if(resultInternal.getNodes().getSize () == 1) {
                                for ( NodeIterator nodeIterator2 = resultInternal.getNodes ( ) ; nodeIterator2.hasNext ( ) ; ) {
                                    Node docItem = null;
                                    String alias = "";
                                    docItem = nodeIterator2.nextNode ( );
                                    alias =  docItem.getProperty (SANTANDERBX_ALIAS).getString ();
                                    docuemntationPayload.setAlias ( alias );
                                    docuemntationPayload.setPath ( docItem.getParent ().getPath () );
                                }
                            }
                            sendDocument ( nodeOriginalDocIndx )   ;
                            documentationPagesPayloadList.add ( docuemntationPayload );
                        }
                        sectionLevelTwoPayload.setDocumentation ( documentationPagesPayloadList );
                    }
                }
                sectionLevelTwoPayloadlist.add ( sectionLevelTwoPayload );
            }

        } catch ( PathNotFoundException e ) {
            LOGGER.error ( "PathNotFoundException processPages dont exist" );

        } catch ( RepositoryException e ) {
            LOGGER.error ( " RepositoryException  processPages dont exist" );

        }
        sectionOnePayload.setSections ( sectionLevelTwoPayloadlist );
    }

    private void processThree ( Node nodeSectionTwo , SectionLevelTwoPayload sectionLevelTwoPayload , Session threadSafeSession ) {
        List < SectionLevelThreePayload > sectionLevelThreePayloadList = new ArrayList < SectionLevelThreePayload > ( );
        NodeIterator nodeSectionThreeIterator = null;
        try {
            nodeSectionThreeIterator = nodeSectionTwo.getNodes ( SANTANDERBRXM_SECTIONS );
            while ( nodeSectionThreeIterator.hasNext ( ) ) {

                SectionLevelThreePayload sectionLevelThreePayload = new SectionLevelThreePayload ( );

                Node nodeSectionThree = nodeSectionThreeIterator.nextNode ( );

                sectionLevelThreePayload.setTitle ( nodeSectionThree.getProperty ( SANTANDERBRXM_TEXT ).getString ( ) );

                //Description Level Three
                Node nodeDescriptionThreeLevel = nodeSectionThree.getNode ( SANTANDERBRXM_HTML );
                DescriptionPayload desciptionPayloadLevelThree = new DescriptionPayload ( );
                desciptionPayloadLevelThree.setContent ( nodeDescriptionThreeLevel.getProperty ( Constants.HIPPO_CONTENT ).getString ( ) );
                sectionLevelThreePayload.setDescription ( desciptionPayloadLevelThree );

                //Si tiene subNodos tiene enlaces internos en la descripcion, los recorremos y los guardamos para exportar
                processInternalLinkDescription(nodeDescriptionThreeLevel,threadSafeSession,desciptionPayloadLevelThree);

                //Section Level Four
                processFour ( nodeSectionThree , sectionLevelThreePayload , threadSafeSession);

                //Documentation Level Trhee
                List < DocumentationPagesPayload > documentationPagesPayloadList = new ArrayList < DocumentationPagesPayload > ( );
                NodeIterator nodeDocumentationIterator = nodeSectionThree.getNodes ( SANTANDERBRXM_DOCUMENTATION );
                while ( nodeDocumentationIterator.hasNext ( ) ) {
                    Node nodeDocumentation = nodeDocumentationIterator.nextNode ( );
                    DocumentationPagesPayload docuemntationPayload = new DocumentationPagesPayload ( );
                    String uuid_doc = nodeDocumentation.getProperty (HIPPO_DOCBASE ).getString ( );
                    Query query = threadSafeSession.getWorkspace().getQueryManager().createQuery( QUERY_DOC+uuid_doc+"']", Query.XPATH);
                    QueryResult result = query.execute();

                    if(result.getNodes().getSize () == 1) {
                        for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                            Node nodeOriginalDocIndx = null;
                            nodeOriginalDocIndx = nodeIterator.nextNode ( );
                            Query queryDocIndex = threadSafeSession.getWorkspace().getQueryManager().createQuery(
                                    QUERY_ROOT+nodeOriginalDocIndx.getPath ()+QUERY_DOC_PUBLISH, Query.XPATH);
                            QueryResult resultInternal = queryDocIndex.execute();
                            if(resultInternal.getNodes().getSize () == 1) {
                                for ( NodeIterator nodeIterator2 = resultInternal.getNodes ( ) ; nodeIterator2.hasNext ( ) ; ) {
                                    Node docItem = null;
                                    String alias = "";
                                    docItem = nodeIterator2.nextNode ( );
                                    alias =  docItem.getProperty (SANTANDERBX_ALIAS).getString ();
                                    docuemntationPayload.setAlias ( alias );
                                    docuemntationPayload.setPath ( docItem.getParent ().getPath () );
                                }
                            }
                            sendDocument ( nodeOriginalDocIndx )   ;
                            documentationPagesPayloadList.add ( docuemntationPayload );
                        }
                        sectionLevelThreePayload.setDocumentation ( documentationPagesPayloadList );

                    }
                }
                sectionLevelThreePayloadList.add ( sectionLevelThreePayload );
            }

        } catch ( PathNotFoundException e ) {
            LOGGER.error ( "PathNotFoundException processPages dont exist" );

        } catch ( RepositoryException e ) {
            LOGGER.error ( " RepositoryException  processPages dont exist" );

        }
        sectionLevelTwoPayload.setSections ( sectionLevelThreePayloadList );
    }

    private void processFour ( Node nodeSectionThree , SectionLevelThreePayload sectionLevelThreePayload , Session threadSafeSession ) {
        List < SectionLevelFourPayload > sectionLevelFourPayloadList = new ArrayList < SectionLevelFourPayload > ( );
        NodeIterator nodeSectionFourIterator = null;
        try {
            nodeSectionFourIterator = nodeSectionThree.getNodes ( SANTANDERBRXM_SECTIONS );
            while ( nodeSectionFourIterator.hasNext ( ) ) {

                SectionLevelFourPayload sectionLevelFourPayload = new SectionLevelFourPayload ( );

                Node nodeSectionFour = nodeSectionFourIterator.nextNode ( );

                //Title Level Four
                sectionLevelFourPayload.setTitle ( nodeSectionFour.getProperty ( "santanderbrxm:tutle" ).getString ( ) );

                //Description Level Four
                Node nodeDescriptionFourLevel = nodeSectionFour.getNode ( Constants.SANTANDERBRXM_DESCRIPTION );
                DescriptionPayload desciptionPayloadLevelFour = new DescriptionPayload ( );
                desciptionPayloadLevelFour.setContent ( nodeDescriptionFourLevel.getProperty ( Constants.HIPPO_CONTENT ).getString ( ) );
                sectionLevelFourPayload.setDescription ( desciptionPayloadLevelFour );

                //Si tiene subNodos tiene enlaces internos en la descripcion, los recorremos y los guardamos para exportar
                processInternalLinkDescription(nodeDescriptionFourLevel,threadSafeSession,desciptionPayloadLevelFour);


                //Documentation Level Four
                List < DocumentationPagesPayload > documentationPagesPayloadList = new ArrayList < DocumentationPagesPayload > ( );
                NodeIterator nodeDocumentationIterator = nodeSectionFour.getNodes ( SANTANDERBRXM_DOCUMENTATION );
                while ( nodeDocumentationIterator.hasNext ( ) ) {
                    Node nodeDocumentation = nodeDocumentationIterator.nextNode ( );
                    DocumentationPagesPayload docuemntationPayload = new DocumentationPagesPayload ( );
                    String uuid_doc = nodeDocumentation.getProperty (HIPPO_DOCBASE ).getString ( );
                    Query query = threadSafeSession.getWorkspace().getQueryManager().createQuery( QUERY_DOC+uuid_doc+"']", Query.XPATH);
                    QueryResult result = query.execute();

                    if(result.getNodes().getSize () == 1) {
                        for ( NodeIterator nodeIterator = result.getNodes ( ) ; nodeIterator.hasNext ( ) ; ) {
                            Node nodeOriginalDocIndx = null;
                            nodeOriginalDocIndx = nodeIterator.nextNode ( );
                            Query queryDocIndex = threadSafeSession.getWorkspace().getQueryManager().createQuery(
                                    QUERY_ROOT+nodeOriginalDocIndx.getPath ()+QUERY_DOC_PUBLISH, Query.XPATH);
                            QueryResult resultInternal = queryDocIndex.execute();
                            if(resultInternal.getNodes().getSize () == 1) {
                                for ( NodeIterator nodeIterator2 = resultInternal.getNodes ( ) ; nodeIterator2.hasNext ( ) ; ) {
                                    Node docItem = null;
                                    String alias = "";
                                    docItem = nodeIterator2.nextNode ( );
                                    alias =  docItem.getProperty (SANTANDERBX_ALIAS).getString ();
                                    docuemntationPayload.setAlias ( alias );
                                    docuemntationPayload.setPath ( docItem.getParent ().getPath () );
                                }
                            }
                            sendDocument ( nodeOriginalDocIndx )   ;
                            documentationPagesPayloadList.add ( docuemntationPayload );
                        }
                        sectionLevelFourPayload.setDocumentation ( documentationPagesPayloadList );
                    }
                }
                sectionLevelFourPayloadList.add ( sectionLevelFourPayload );
            }
        } catch ( PathNotFoundException e ) {
            LOGGER.error ( "PathNotFoundException processPages dont exist" );

        } catch ( RepositoryException e ) {
            LOGGER.error ( " RepositoryException  processPages dont exist" );

        }
        sectionLevelThreePayload.setSections ( sectionLevelFourPayloadList );
    }

    private  byte[] encodeImageThumbnail ( Node nodeImage ) throws IOException, RepositoryException {

        //Thunmbnail Encode Base64
        Binary binaryThumbnail = nodeImage.getNode ( "hippogallery:thumbnail" ).getProperty ( "jcr:data" ).getBinary ( );
        InputStream isThumbnail = binaryThumbnail.getStream ( );
        byte[] fileContentThumbnail = IOUtils.toByteArray ( isThumbnail );
       //String base64Thumbnail = Base64.getEncoder ( ).encodeToString ( fileContentThumbnail );

        return fileContentThumbnail;
    }

    private  byte[] encodeImageOriginal ( Node nodeImage ) throws RepositoryException, IOException {

        //Origina Encode Base64
        Binary binaryOriginal = nodeImage.getNode ( "hippogallery:original" ).getProperty ( "jcr:data" ).getBinary ( );
        InputStream isOriginal = binaryOriginal.getStream ( );
        byte[] fileContentOriginal = IOUtils.toByteArray ( isOriginal );
       //String base64Original = Base64.getEncoder ( ).encodeToString ( fileContentOriginal );

        return fileContentOriginal;

    }

    private QueryResult obtainResult ( Session threadSafeSession , Node nodeIcon ) throws RepositoryException {
        Query query = null;
        query = threadSafeSession.getWorkspace ( ).getQueryManager ( )
                .createQuery ( QUERY_IMAGE + nodeIcon.getProperty ( Constants.HIPPO_DOCBASE ).getString ( ) + "']" , Query.XPATH );

        return query.execute ( );
    }
}