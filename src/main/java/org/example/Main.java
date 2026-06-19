package org.example;

import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.apache.catalina.Context;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception{
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8083);

        tomcat.getHost().setAppBase("target");

        Context ctx =tomcat.addWebapp("/dashboard", new File("target/Dashboard-1.0-SNAPSHOT").getAbsolutePath());
        StandardJarScanner scanner = (StandardJarScanner) ctx.getJarScanner();
        scanner.setScanManifest(false);
        scanner.setScanAllDirectories(false);
        scanner.setScanAllFiles(false);

        tomcat.start();
        tomcat.getServer().await();
    }
}