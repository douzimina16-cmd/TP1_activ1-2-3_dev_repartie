package clientPackage;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static java.net.InetAddress.getByName;

public class Client {
    static final String IP_SERVEUR = "192.168.1.2"; // adresse IP de la machine serveur
    static final int PORT = 1234;
    // les différents choix du menu
    public static final int quitter = 0;
    public static final int addition = 1;
    public static final int soustraction = 2;
    public static final int multiplication = 3;
    public static final int division = 4;
    //affiche le menu à l'écran
    static void afficherMenu() {
        System.out.println();
        System.out.println("===== Calculatrice distante =====");
        System.out.println("1 - Addition        (x + 5)");
        System.out.println("2 - Soustraction    (x - 5)");
        System.out.println("3 - Multiplication  (x * 5)");
        System.out.println("4 - Division        (x / 5)");
        System.out.println("0 - Quitter");
        System.out.println("Votre choix : ");
    }
    static String symbole(int op) {
        switch (op) {
            case addition:       return "+";
            case soustraction:   return "-";
            case multiplication:return "*";
            default:             return "/";
        }
    }
    public static void main(String[] args){
        // La première étape :
        try {
        System.out.println("Je suis un client pas encore connecté…");
        Socket socket=new Socket(IP_SERVEUR,PORT);
        System.out.println("je suis un client connecté");
        //Le Client est demandé de taper un entier x au clavier
            Scanner scanner =new Scanner(System.in);

            System.out.println("tapez un entier x:");
            int x= scanner.nextInt();
            //le client envoie cet entier au serveur
            OutputStream os=socket.getOutputStream();
            DataOutputStream dos=new DataOutputStream(os);
            dos.write(x);
            System.out.println("nombre envoyé au serveur:"+x);

            //le client lire le resultat et reste avoir un x tant que diff de 0
            InputStream is=socket.getInputStream();
            DataInputStream dis=new DataInputStream(is);
            int xx=dis.readInt();
            System.out.println("resultat recu du serveur"+xx);
            do {
                System.out.println("Entrez un entier (0 pour arrêter) : ");
                 x=scanner.nextInt();

                dos.writeInt(x);
               // Le processus continue tant que l’utilisateur saisit un entier différent de 0.
                //d) Lorsque l’utilisateur saisit 0, la communication entre le client et le serveur
                //s’arrête proprement, et les sockets sont fermés des deux côtés
                if(x!=0)
                {
                    int res= dis.readInt();
                    System.out.println(x+" *5"+res);}
            }
            while (x!=0);
            scanner.close();
            dis.close();
            dos.close();
            socket.close();
          //récupérer l'adresse IP du serveur
            Scanner a = new Scanner(System.in);
            try {
                String ipServeur;
                if (args.length >= 1) {
                    ipServeur = args[0];
                } else {
                    System.out.print("Adresse IP du serveur : ");
                    ipServeur = a.nextLine().trim();
                }
                //int port = (args.length >= 2) ? Integer.parseInt(args[1]) :1234;
                //Le port permet d'identifier le service auquel le client veut se connecter sur la machine distante
                int port;
                if (args.length >= 2) {
                    port = Integer.parseInt(args[1]);
                } else {
                    port = 1234;
                }
                //préparer l'adresse de connexion
                /*InetAddress adresse = null;
                try {
                    adresse = getByName(ipServeur);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                InetSocketAddress adresseServeur = new InetSocketAddress(adresse, port);

                try (Socket socketd = new Socket()) {
                    socket.connect(adresseServeur, 5000);     // un délai maximal de connexion
                    System.out.println("Client Connecté à " + adresseServeur);*/
//Créer les flux de communication
                    InputStream iss=socket.getInputStream();
                    DataInputStream diss = new DataInputStream(iss);
                    OutputStream ot=socket.getOutputStream();
                    DataOutputStream out = new DataOutputStream(ot);
                    int choix;
                    do {
                        afficherMenu();
                        try {
                            choix = a.nextInt();
                        } catch (Exception e) {
                            a.next();
                            System.out.println("Choix invalide");
                            choix = -1;
                            continue;
                        }

                        if (choix == quitter) {
                            ot.write(quitter);
                            ot.flush();
                        } else if (choix >= addition && choix <=division) {
                            System.out.print("Entrez la valeur x : ");
                            int r;
                            try {
                                r = a.nextInt();
                            } catch (Exception e) {
                                a.next();
                                System.out.println("Valeur invalide : un entier est attendu");
                                continue;
                            }
                            out.writeInt(choix);
                            out.writeInt(r);


                            double resultat = diss.readDouble();
                            System.out.println("Résultat : " + x + " " + symbole(choix) + " 5 = "
                                    + resultat);
                        } else {
                            System.out.println("Choix invalide ");
                        }
                    } while (choix != quitter);//'effectuer plusieurs opérations sans devoir relancer le client

                    System.out.println("Client Communication terminée");

            } catch (IOException | NumberFormatException e) {
                System.err.println("client Erreur : " + e.getMessage());
            }



// La dernière étape
     socket.close();
        } catch (IOException e) {
            System.out.println("erreur"+e.getMessage());
        }

    }
}
