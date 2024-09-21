public class Main {
    public static void main(String[] args) {
        System.out.println("args has " + args.length + " arguments");
        for (var a : args)
            System.out.println("    " + a);
    }
}
