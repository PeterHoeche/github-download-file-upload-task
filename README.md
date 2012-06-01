github-download-file-upload-task
================================
This library provides an Ant Task which let you upload files to the download section of a github repository.

**Dependencies**

The Ant Task is compiled against *gson-2.2.1.jar* and *org.eclipse.egit.github.core-1.2.1-20120113.162445-8.jar*. The JARs are included in the download file.

**Download**

Download the latest version from [the downloads page](https://github.com/PeterHoeche/github-download-file-upload-task/downloads). The zip file contains all necessary JAR files.

Usage
-----

***Properties***

* **user** (required) - The name of the uploading user.
* **password** (required) - The password of the uploading user.
* **repositoryOwner** (required) - The owner of the repository where the file should be published.
* **repositoryName** (required) - The name of the repository where the file should be published.
* **fileName** (required) - The url to the file which should be published.
* **fileDescription** (optional) - An optional short description for the file.
* **override** (optional) - Set this optional property to true, to override existing files on the server. Default value is false. (Be careful, existing files will be deleted first!)

***Sample Project***

	<project name="sample-project">

		<path id="ant.libs">
			<fileset dir="./ant" includes="**/*.jar" />
		</path>

		<taskdef name="github-download-file-upload" 
			classname="de.hopa.github.fileupload.GithubDownloadFileUploadTask" 
			classpathref="ant.libs"
		/>

		<target name="upload-files-to-github">

			<github-download-file-upload 
				user="${GITHUB_USER}"
				password="${GITHUB_USER_PASSWORD}"
				repositoryowner="${repository.owner}"
				repositoryname="${project.title}"
				fileurl="${basedir}/../build/${binary.name}.zip"
				description="artefacts of sample-project"
				override="true"
			/>
		</target>

	</project>


Developer
----------
The 'github-download-file-upload-task' was developed by [Peter Höche](https://github.com/PeterHoeche/) and [Stephan Partzsch](https://github.com/StephanPartzsch/).