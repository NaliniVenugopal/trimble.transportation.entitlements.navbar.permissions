@Library('slipway-shared-library') _

import com.trimble.slipway.*

podTemplate(inheritFrom: 'dind-rootless-slipway') {

    node(POD_LABEL) {
        
        sharedLibAdaptor = new SharedLibraryAdaptor(SlipwayConstants, this)
        
        buildLib = sharedLibAdaptor.getBuildController()
        dockerLib = sharedLibAdaptor.getDockerController()
        gitLibrary = sharedLibAdaptor.getGitController()
        gChatLib = sharedLibAdaptor.getGoogleChatController()
        scannerLib = sharedLibAdaptor.getScannerController()
        
        buildLib.setBuildDiscarderProperties(SlipwayConstants.BuildDiscarderNumberofDays, SlipwayConstants.BuildDiscarderNumberofBuilds)

        container(SlipwayConstants.JenkinsContainerName) {

            try {

                stage(SlipwayConstants.StageCheckoutGitRepo) {
                    deleteDir()
                    checkout scm
                }

                stage(SlipwayConstants.StageConfigureBuildEnv) {

                    sharedLibAdaptor.setupBuildEnvironment()

                    pipelineName = sh(returnStdout: true, script: "echo ${buildLib.getJobName()} | cut -d '/' -f 1", label: "Get pipeline name").trim()
                    pipelineBuildStatusUrl = "${buildLib.getHudsonURL()}blue/organizations/jenkins/${pipelineName}/detail/${buildLib.getBranchName()}/${buildLib.getBuildNumber()}/pipeline"
                    isReleaseBranchTrueFalse = sh(returnStdout: true, script: "if [ ${buildLib.getBranchName()} = 'master' ] || [ ${buildLib.getBranchName()} = 'release' ]; then echo -n 'true'; else echo -n 'false'; fi", label: "Check if branch is a release branch")
                    buildUuid = sh(returnStdout: true, script: 'echo -n "jenkins-$(uuidgen)"', label: 'Generate CI build UUID')

                    println "CI Build UUID = ${buildUuid}"
                    println "Image Repository = ${dockerLib.getImageRepository()}"
                    println "GIT Repo NAME = ${gitLibrary.getBitbucketRepoName()}"
                    println "Image Tag Version Major Minor = ${dockerLib.getImageTagVersionMajorMinor()}"
                    println "Image Tag Version Major MinorPatch = ${dockerLib.getImageTagVersionMajorMinorPatch()}"
                    println "Image Tag Version Full = ${dockerLib.getImageTagVersionFull()}"
                    println "Scan [Sonarqube + Whitesource] = ${buildLib.ifPerformScan()}"
                    println "Artifactory Service = ${buildLib.ifArtifactoryService()}"

                }

                stage(SlipwayConstants.StageWaitForDockerEnginer) {
                    buildLib.waitForDocker()
                }

                stage(SlipwayConstants.StageBitbucketChatNotificationProgress) {
                    SendNotification(SlipwayConstants.GchatNotificationJobStart)
                    UpdateBuildStatus(SlipwayConstants.BitbucketStatusProgress, SlipwayConstants.BitbucketStatusMsgProgress)
                }

                stage(SlipwayConstants.StageDockerBuild) {
                    SendNotification(SlipwayConstants.GchatNotificationDockerBuildStart)
                    ExecuteDocker("build")
                    SendNotification(SlipwayConstants.GchatNotificationDockerBuildCompleted)
                }

                stage(SlipwayConstants.StageDockerTag) {
                    ExecuteDocker("tag")
                }

                stage(SlipwayConstants.StageSetBuildNameTag) {
                    buildLib.setBuildDisplayName("${dockerLib.getImageTagVersionMajorMinorPatch()}-${gitLibrary.getGitCommitShort()}")
                    if (buildLib.ifMasterBranch()) {
                        gitLibrary.addBitbucketTag()
                    }
                }

                Integer imageCount = sh(returnStdout: true, script: "docker images | grep ${dockerLib.getImageTagVersionFull()} | wc -l | xargs").trim()
                
                if (!buildLib.ifMasterBranch() || buildLib.ifArtifactoryService()) {
                    if (buildLib.ifPerformScan()) {
                        stage(SlipwayConstants.StageDockerPush) {
                            ExecuteDocker("push")
                        }
                        ExecuteScans(imageCount)
                        stage(SlipwayConstants.StageCleanUpImages) {
                            try {
                                dockerLib.flushTempDockerImages(imageCount)
                                SendNotification(SlipwayConstants.CleanupImagesPassed)
                            } catch(Exception exception) {
                                SendNotification(SlipwayConstants.CleanupImagesFailed)
                                println "Flushing temporary images has failed. Please login to Google cloud and flush images manually."
                                echo exception.toString()
                                currentBuild.result = "SUCCESS"
                            }
                        }
                    }
                } else {
                    stage(SlipwayConstants.StageDockerPush) {
                        ExecuteDocker("push")
                    }
                    ExecuteScans(imageCount)
                }

                if (buildLib.ifArtifactoryService()) {
                    stage (SlipwayConstants.StageArtifactoryUpload) {
                        if (buildLib.ifMasterBranch()) {
                            try {
                                dockerLib.publishToArtifactory()
                                SendNotification(SlipwayConstants.ArtifactoryPublishPassed)
                            } catch(Exception exception) {
                                SendNotification(SlipwayConstants.ArtifactoryPublishFailed)
                                println "Publishing manifests to artifactory has failed."
                                echo exception.toString()
                            }
                        } else {
                            println "Not Master Branch ; Skipping Artifactory Publish"
                        }
                    }
                }

                stage(SlipwayConstants.StageBitbucketChatNotificationCompleted) {
                    SendNotification(SlipwayConstants.GchatNotificationJobCompleted)
                    UpdateBuildStatus(SlipwayConstants.BitbucketStatusSuccessful, SlipwayConstants.BitbucketStatusMsgSuccessful)
                }

            } catch (Exception exception) {
                SendNotification(SlipwayConstants.GchatNotificationJobFailed)
                UpdateBuildStatus(SlipwayConstants.BitbucketStatusFailed, SlipwayConstants.BitbucketStatusMsgFailed)
                buildLib.setBuildDisplayName("${dockerLib.getImageTagVersionMajorMinorPatch()}-${gitLibrary.getGitCommitShort()}")
                println exception
                error "BUILD FAILED"
            }
        }
    }
}

def SendNotification(String notificationMessage) {
    gChatLib.setNotificationMessage(notificationMessage)
    gChatLib.sendNotification()
}

def UpdateBuildStatus(String status, String statusMessage) {
    buildLib.setBuildStatus(status)
    buildLib.setBuildStatusMessage(statusMessage)
    gitLibrary.updateBuildStatus()
}

def ExecuteDocker(String dockerAction) {
    if (buildLib.ifMonoRepo()) {
        dockerLib.performDockerMonoRepoAction(dockerAction)
    } else {
        dockerLib.performDockerAction(dockerAction)
    }
}

def ExecuteScans(Integer imageCount) {
    def tasks = [:]
    try {
        tasks[SlipwayConstants.TaskSonarqube] = {
            scannerLib.performSonarScan(imageCount)
        }
        tasks[SlipwayConstants.TaskWhitesource] = {
            scannerLib.performWhitesourceScan(imageCount)
        }
    parallel tasks
    } catch(Exception exception) {
        println "Sonarqube or Whitesource scans has failed.. Skipping to proceed with build execution."
        echo exception.toString()
        currentBuild.result = "SUCCESS"
    }
}
