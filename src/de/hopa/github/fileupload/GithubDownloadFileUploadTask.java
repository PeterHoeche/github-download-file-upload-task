package de.hopa.github.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.DownloadResource;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.RepositoryService;

public class GithubDownloadFileUploadTask extends Task
{
	private String user;
	private String password;
	
	private String repositoryOwner;
	private String repositoryName;
	
	private File file;
	private String fileDescription = "";
	private Boolean override = false;
	
	private GitHubClient client;
	private Repository repository;
	
	private RepositoryService repositoryService;
	private DownloadService downloadService;
	
	public void setUser( String user )
	{
		this.user = user;
	}
	
	public void setPassword( String password )
	{
		this.password = password;
	}
	
	public void setRepositoryOwner( String repositoryOwner )
	{
		this.repositoryOwner = repositoryOwner;
	}
	
	public void setRepositoryName( String repositoryName )
	{
		this.repositoryName = repositoryName;
	}
	
	public void setFileUrl( String fileUrl )
	{
		this.file = new File( fileUrl );
	}

	public void setFileDescription( String fileDescription )
	{
		this.fileDescription = fileDescription;
	}
	
	public void setOverride(Boolean override)
	{
		this.override = override;
	}
	
	@Override
	public void execute() throws BuildException
	{
		initializeGitHubClient();
		startUploadFileProcess();
	}

	private void initializeGitHubClient()
	{
		try
		{
			client = new GitHubClient();
			client.setCredentials( user, password );
		
			repositoryService = new RepositoryService();
			downloadService = new DownloadService( client );
		
			repository = repositoryService.getRepository( repositoryOwner, repositoryName );
		} 
		catch ( IOException e )
		{
			e.printStackTrace();
			throw new BuildException( "An error occured during initialization of GitHubClient!" );
		}
	}
	
	private void startUploadFileProcess()
	{
		try
		{
			uploadFile();
		} 
		catch ( IOException e )
		{
			System.err.println( "Something went wrong maybe it's the following problem: ");
			System.err.println( "File '" + file.getName() + "' already exists!" );
			e.printStackTrace();
			
			if ( override )
			{
				try
				{
					deleteFile();
					uploadFile();
				} 
				catch ( IOException e1 )
				{
					throw new BuildException( "Could not upload resource with name '" + file.getName() + "'!" );
				}
			}
			else
			{
				throw new BuildException( "Download resource with name '" + file.getName() + "' already exists" );
			}
		}
	}
	
	private void uploadFile() throws IOException
	{
		System.err.println( "Uploading file '" + file.getName() + "'!" );
		
		FileInputStream content = new FileInputStream( file );
		
		downloadService.uploadResource( createDownloadResource(), content, file.length() );
	}
	
	private DownloadResource createDownloadResource() throws IOException
	{
		Download download = new Download();
		download.setName( file.getName() );
		download.setSize( file.length() );
		download.setDescription( fileDescription );
		
		return downloadService.createResource( repository, download );
	}
	
	private void deleteFile() throws IOException
	{
		System.err.println( "Deleting file '" + file.getName() + "'!" );
		
		downloadService.deleteDownload( repository, getDownloadIdForName( file.getName() ) );
	}

	private int getDownloadIdForName( String name ) throws IOException
	{
		List<Download> downloads = downloadService.getDownloads( repository );
		
		for ( Iterator<Download> iterator = downloads.iterator(); iterator.hasNext(); )
		{
			Download download = (Download) iterator.next();
			
			if ( name.equals( download.getName() ) )
				return download.getId();
		}
		
		return 0;
	}
}
