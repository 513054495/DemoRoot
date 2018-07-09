package com.infinitus.husky;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

public class GitUtils {

    public static Git git ;

    public static void cloneProjectToLocal(String remoteRepoURI,String localRepoPath) throws GitAPIException {
        git = Git.cloneRepository().setURI(remoteRepoURI).setDirectory(new File(localRepoPath)) .call();
        git.close();
    }

    public static void commitProject(String localRepoPath,String filepatterns,String msg,String userName,String password) throws IOException, GitAPIException {
        CredentialsProvider cp = new UsernamePasswordCredentialsProvider(userName, password);
        String[] filepatternList=filepatterns.split(",");
        git=Git.open( new File(localRepoPath) );
        //创建用户文件的过程
        for (String filepattern :filepatternList) {
            git.add().addFilepattern(filepattern).call();
        }
        //提交
        git.commit().setMessage(msg).call();
        //推送到远程
        git.push().setCredentialsProvider(cp).call();
        git.close();
    }
}
