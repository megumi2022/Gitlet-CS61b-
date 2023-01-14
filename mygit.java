import java.util.Arrays;
import java.util.List;

public class mygit {
    public static void main(String[] args) throws Exception {
        List<String> cmdList = Arrays.asList("init", "add", "commit", "rm", "log", "reset", "pull", "push");
        String sign = null;
        boolean res = false;
        if (args.length < 1) {
            sign = "命令长度输入错误！";
        } else if (!cmdList.contains(args[0])) {
            sign = "本程序不具有该命令功能，请检查输入！！";
        } else {
            switch (args[0]) {
                case "init":
                    System.out.println("init命令执行");
                    res = command.init();
                    sign = res == true?"init命令执行成功":"init命令执行失败";
                    break;
                case "add":
                    if (args[1].equals(".")) {
                        System.out.println("add.命令执行");
                        res = command.addDot();
                        sign = res == true?"add.命令执行成功":"add.命令执行失败";
                    } else {
                        System.out.println("add命令执行");
                        res = command.add(args[1]);
                        sign = res == true ? "add命令执行成功" : "add命令执行失败";
                    }
                    break;
                case "commit":
                        System.out.println("commit命令执行");
                        res = command.commit(args[1]);
                        sign = res == true ? "commit命令执行成功" : "commit命令执行失败";
                    break;
                case "rm":
                    if(args.length > 1 && args[1].equals("--cached")){
                        System.out.println("rm--cached命令开始执行");
                        res = command.rmc(args[2]);
                        sign = res == true?"rm--cached命令执行成功":"rm--cached命令执行失败";
                }else{
                        System.out.println("rm命令开始执行");
                        res = command.rm(args[1]);
                        sign = res == true?"rm命令执行成功":"rm命令执行失败";
                    }
                    break;
                case "log":
                    System.out.println("log命令开始执行");
                    res = command.log();
                    sign = res == true?"log命令执行成功":"log命令执行失败";
                    break;
                case "reset":
                    if(args.length > 1 && args[1].equals("--hard")){
                        System.out.println("reset--hard命令开始执行");
                        res = command.resetHard(args[2]);
                        sign = res == true?"resetHard命令执行成功":"resetHard命令执行失败";
                    }else if(args.length > 1 && args[1].equals("--soft")){
                        System.out.println("resetSoft命令开始执行");
                        res = command.resetSoft(args[2]);
                        sign = res == true?"resetSoft命令执行成功":"resetSoft命令执行失败";
                    }else{
                        System.out.println("reset--mixed命令开始执行");
                        res = command.resetMixed(args[1]);
                        sign = res == true?"resetMixed命令执行成功":"resetMixed命令执行失败";
                    }
                    break;
                case "pull":
                    System.out.println("pull命令开始执行");
                    res = command.pull();
                    sign = res == true?"pull命令执行成功":"pull命令执行失败";
                    break;
                case "push":
                    System.out.println("push命令开始执行");
                    res = command.push();
                    sign = res == true?"push命令执行成功":"push命令执行失败";
                    break;
                default:
                    System.out.println("无法执行，请检查错误！");
            }
        }
        System.out.println(sign);
    }
}
