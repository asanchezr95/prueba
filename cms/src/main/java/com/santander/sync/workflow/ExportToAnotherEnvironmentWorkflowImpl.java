package com.santander.sync.workflow;

import java.rmi.RemoteException;

import javax.jcr.RepositoryException;

import org.hippoecm.repository.ext.WorkflowImpl;

public class ExportToAnotherEnvironmentWorkflowImpl extends WorkflowImpl
implements ExportToAnotherEnvironmentWorkflow {

public ExportToAnotherEnvironmentWorkflowImpl() throws RemoteException {
	// do nothing
}

public void feature() throws 
	RepositoryException {
		getNode().setProperty("featured:isFeatured", true);
	}
}


