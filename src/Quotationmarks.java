public class Quotationmarks {
    static String last;
    public static void main(String[] args) {
        String x = "Test \" test blah blah blah \" test";
        while(x.indexOf("\"") != -1) {
            int spot = x.indexOf("\"");
            System.out.println(spot);
            last = x.substring(0, spot);
            System.out.println(x);
            x = last + x.substring(spot + 1);
        }
        System.out.println(x);


    }
}
