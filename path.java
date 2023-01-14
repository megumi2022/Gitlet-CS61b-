import java.io.File;

public class path {
    private static final String _GIT = "F:"+ File.separator+"gitlet"+ File.separator+".git";
    private static final String _INDEX = "F:"+ File.separator+"gitlet"+ File.separator+".git"+ File.separator+"index";
    private static final String _HEAD = "F:"+ File.separator+"gitlet"+ File.separator+".git"+ File.separator+"HEAD";
    private static final String _OBJETCS = "F:"+ File.separator+"gitlet"+ File.separator+".git"+ File.separator+"Objects";
    private static String _FILENAME;
    private static final String _WORKPLACE = "F:"+ File.separator+"gitlet"+File.separator+"workplace";

    private static final String _SERVERFILE = "F:"+ File.separator+"gitlet"+ File.separator+"ServerFile";
    private static final String _GITLET = "F:"+ File.separator+"gitlet";

    public static String getGitlet(){
        return _GITLET;
    }

    public static String getServerfile(){
        return _SERVERFILE;
    }

    public static String getWorkplace() {
        return _WORKPLACE;
    }

    public static String get_GIT() {
        return _GIT;
    }

    public static String get_INDEX() {
        return _INDEX;
    }

    public static String get_HEAD() {
        return _HEAD;
    }

    public static String get_OBJETCS() {
        return _OBJETCS;
    }

}
