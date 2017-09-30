/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rsa;
import java.math.BigInteger;
import java.util.*;
/**
 *
 * @author lizhengxing
 */
public class MainApp {
        static Scanner sc = new Scanner(System.in);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Start the program
        runApp();

    }
    
    
    //Display the available options
    public static int menu()
    {
        System.out.println("Welcome! Please select option to proceed.");
        System.out.println("1. Create Key Pairs");
        System.out.println("2. Encrypt Message");
        System.out.println("3. Decrypt Message");
        System.out.println("4. Display Keys");
        System.out.println("5. Exit");
        System.out.print("\nOption: ");
        int option = sc.nextInt();
        return option;
    }
    
    public static void runApp()
    {
        RSA rsa = new RSA();
        int option = 0;
        while(option != 5)
        {
            option = menu();
            if(option >5 || option < 0)
            {
                System.out.println("Invalid input!");
            }
            if(option ==1 )
            {
                System.out.print("Please enter a PRIME number: ");
                int p = sc.nextInt();
                System.out.print("Please enter another PRIME number: ");
                int q = sc.nextInt();
                rsa.createKyes(p, q);
            }
            if(option == 2)
            {
                boolean b = rsa.isKeyCreated();
                if( b == false)
                {
                    System.out.println("Please create key pairs first!");
                }
                else
                {
                    System.out.println("Please enter the message you wish to encrypt: ");
                    String temp = sc.nextLine();
                    String m = sc.nextLine();
                    String em = rsa.encryptMessage(m);
                    System.out.println("Encoded message: "+em);
                }
            }
            if(option == 3)
            {
                boolean b = rsa.isKeyCreated();
                if( b == false)
                {
                    System.out.println("Please create key pairs first!");
                }
                else
                {
                    System.out.println("Please enter the message you wish to decrypt: ");
                    String temp = sc.nextLine();
                    String m = sc.nextLine();
                    String dm = rsa.decryptMessage(m);
                    System.out.println("Decoded message: "+dm);
                }
            }
            if(option ==4 )
            {
                boolean b = rsa.isKeyCreated();
                if( b == false)
                {
                    System.out.println("Please create key pairs first!");
                }
                else
                {
                    System.out.println(rsa.displayKey());
                }
                
            }
            if(option ==5 )
            {
                System.out.println("Bye!");
            }
            printStars();
        }
    }
    
    public static void printStars() {
        System.out.print("\n****************************************\n");
    }
    
}


class RSA {

    //instance variables
    private BigInteger[] publicKey = new BigInteger[2];
    private BigInteger[] privateKey = new BigInteger[2];
    private Scanner sc = new Scanner(System.in);

    //Consturctor
    public RSA() {
    }

    
    
    public void createKyes(int p, int q) {
        BigInteger P = new BigInteger(Integer.toString(p));
        BigInteger Q = new BigInteger(Integer.toString(q));

        boolean valid = false;

        //to check if p and q are prime and not euqal
        while (valid) {
            if (P.equals(Q)) {
                System.out.println("Num1 and num2 shouldn't be equal.");
                System.out.print("num1 = ");
                P = new BigInteger(sc.nextLine());
                System.out.print("num2 = ");
                Q = new BigInteger(sc.nextLine());
            }
            if (!isPrime(P)) {
                System.out.println("Num1 is not a prime number.");
                System.out.print("num1 = ");
                P = new BigInteger(sc.nextLine());
            }
            if (!isPrime(Q)) {
                System.out.println("Num2 is not a prime number.");
                System.out.print("num2 = ");
                Q = new BigInteger(sc.nextLine());
            }
            if (!P.equals(Q) && isPrime(Q) && isPrime(P)) {
                valid = true;
            }
        }

        //calculate phi N
        BigInteger N = P.multiply(Q);
        BigInteger phi_N = P.subtract(BigInteger.ONE);
        phi_N = phi_N.multiply(Q.subtract(BigInteger.ONE));
        System.out.print("Enter another number: ");
        String e = sc.nextLine();
        BigInteger E = new BigInteger(e);  
        //to check if phi N and e are co-prime
        BigInteger gcd = E.gcd(phi_N);
        while (!gcd.equals(BigInteger.ONE)) {
            System.out.print("E and phi N are not co-prime, please enter again: ");
            E = new BigInteger(sc.nextLine());
            gcd = E.gcd(phi_N);
        }

        //put public key and private key into two arrays
        this.publicKey[0] = E;
        this.publicKey[1] = N;

        BigInteger D = E.modInverse(phi_N);
        this.privateKey[0] = D;
        this.privateKey[1] = N;

        System.out.print(displayKey());

    }

    public String encryptMessage(String message) {
        //break message into characters and store them in an array
        char[] mArr = message.toCharArray();
        //convert each character to a numeric value
        ArrayList<String> preparedMessage = convertNumber(mArr);
        String m = "";
        //encrypt each character
        for (int i = 0; i < preparedMessage.size(); i++) {
            String M = encode(preparedMessage.get(i));
            if(M.length()<this.publicKey[1].toString().length())
            {
                while(this.publicKey[1].toString().length()-1>=M.length())
                {
                    M= "0"+M;
                }
            }
            m+=M;
        }
        return m;

    }

    public String decryptMessage(String message) {

        int n =this.publicKey[1].toString().length();
        String text = "";
        //decrypt each block of cipher text with size equal to the size of N
        for(int i=0 ;i<message.length();i+=n)
        {
            String num = message.substring(i,i+n);
            int m = Integer.parseInt(decode(num));
            text += getLetterByNum(m);
            
        }
        return text;
    }

    //use modPow() function to encode and decode a single character with public key and private key repectively
    private String decode(String num) {
        BigInteger Cn = new BigInteger(num);
        BigInteger Mn = Cn.modPow(this.privateKey[0], this.privateKey[1]);
        return Mn.toString();

    }

    private String encode(String num) {
        BigInteger Mn = new BigInteger(num);
        BigInteger Cn = Mn.modPow(this.publicKey[0], this.publicKey[1]);
        return Cn.toString();
    }

    //convert the decoded numeric value to a character
    private String getLetterByNum(int num) {
        
        return Character.toString((char)num);
    }

    //convert each character to a numeric value which points to a character in ASCHII table
    private ArrayList<String> convertNumber(char[] message) {
        ArrayList<String> strArr = new ArrayList<String>();
        for (int i = 0; i < message.length; i++) {

            char c = message[i];
            String str =Integer.toString((int)c);
            strArr.add(str);
        }
        return strArr;
    }
    //display public key and private key
    public String displayKey() {
        return "Public Key: [" + this.publicKey[0] + " , " + this.publicKey[1] + "]"
                + "\nPrivate Key: [" + this.privateKey[0] + " , " + this.privateKey[1] + "]\n";

    }

    //to check if the number enetered by user is prime
    private boolean isPrime(BigInteger num) {
        return num.isProbablePrime(10);
    }
    
    //to check if public key and private key are created
    public boolean isKeyCreated()
    {
        boolean b = true;
        int count = 0;
        while(b==true && count<this.privateKey.length)
        {
            if(this.privateKey[count]==null)
            {
                b = false;
            }
            count ++;
        }
        count =0;
        while(b==true && count<this.publicKey.length)
        {
            if(this.publicKey[count]==null)
            {
                b = false;
            }
            count ++;
        }        
        return b;
    }
    
}
