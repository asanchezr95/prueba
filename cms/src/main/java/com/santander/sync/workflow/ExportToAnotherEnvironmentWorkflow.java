package com.santander.sync.workflow;


import javax.jcr.RepositoryException;


@FunctionalInterface
public interface ExportToAnotherEnvironmentWorkflow {
    
	void feature() throws RepositoryException;
	
}
