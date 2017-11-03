import groovy.io.FileType

def gitrepo = "git@gitrepo:dir/repo.git"

def list = []

def dir = new File(new File(__FILE__).parent + "/pipelines/")

dir.eachFileRecurse(FileType.FILES) { file ->
    out.println file.canonicalPath
    list << file
}
list.each {
    def jobname = it.getName().replaceFirst(~/\.[^\.]+$/, '') // less .ext
    String pathInRepo = new File(new File(__FILE__).parent).toPath().relativize(it.toPath()).toString()
    def pos = pathInRepo.lastIndexOf("/")
    def jobFolder = pathInRepo.substring(0, pos).replaceFirst(~/pipelines\//, '')
    //def jobFolder = it.getParentFile().getAbsolutePath().replaceFirst(~/.+pipelines\//, '') // jenkins-folders
    if (jobFolder != "") {
        if (jobFolder.contains("/")) {
            def folders = jobFolder.split('/')
        } else {
            folder(jobFolder)
        }
    }
    String jobNameWithFolder
    if (jobFolder == "") {
        jobNameWithFolder = jobname
    } else {
        jobNameWithFolder = jobFolder + "/" + jobname
    }
    out.println('job: ' + jobNameWithFolder)
    out.println('pathInRepo: ' + pathInRepo)
    pipelineJob(jobNameWithFolder) {
        definition {
            cpsScm {
                scm {
                    git {
                        remote {
                            name('origin')
                            url(gitrepo)
                            // credentials('string')
                        }
                        branch('master')
                        extensions {
                            cleanBeforeCheckout()
                        }
                    }
                }
                scriptPath(pathInRepo)
            }
        }
    }
}

