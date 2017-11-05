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
    String pathInRepo = new File(new File(__FILE__).parent).toPath().relativize(it.toPath()).toString().replaceAll("\\\\", "/")
    def pos = pathInRepo.lastIndexOf("/")
    def jobFolder = ""
    if (pos != -1) {
        jobFolder = pathInRepo.substring(0, pos).replaceFirst(~/pipelines\//, '')
    }
//    def jobFolder = it.getParentFile().getAbsolutePath().replaceAll("\\\\", "/").replaceFirst(~/.+pipelines\//, '') // jenkins-folders
    splitter(jobFolder).each {
        if (it != "pipelines")
            folder(it)
    }
    String jobNameWithFolder
    if (jobFolder == "pipelines") {
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

static List<String> splitter(String folder) {
    List<String> ret = new ArrayList<>()
    int lastIndex = 0
    while (lastIndex < folder.length()) {
        int currentIndex = folder.indexOf("/", lastIndex + 1)
        if (currentIndex == -1) {
            currentIndex = folder.length()
        }
        ret.add(folder.substring(0, currentIndex))
        lastIndex = currentIndex
    }
    return ret
}
