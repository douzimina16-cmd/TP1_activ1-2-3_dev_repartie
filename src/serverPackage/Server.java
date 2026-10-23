package serverPackage;

import javax.sound.sampled.Port;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class Server {
//Ces constantes définissent les codes que le client peut envoyer
    public static final int oper = 5;

    public static final int quitter = 0;
    public static final int addition = 1;
    public static final int soustraction = 2;
    public static final int multiplication = 3;
    public static final int division = 4;
    //cherche une adresse IPv4 utilisable de la machine sur laquelle le serveur s'exécute
    static InetAddress trouverAdresseReelle() throws IOException {
        //récupérer les interfaces réseau
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            //ignorer les interfaces non adaptées
            //ni.isUp():l'interface n'est pas active
            //ni.isLoopback():c'est une interface de boucle locale
            //ni.isVirtual() : c'est une interface virtuelle.
            //
            //continue : on passe à l'interface suivante
            if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                continue;
            }
            Enumeration<InetAddress> adresses = ni.getInetAddresses();
            while (adresses.hasMoreElements()) {
                InetAddress adr = adresses.nextElement();
                //rechercher une adresse IPv4
                if (adr instanceof Inet4Address && !adr.isLoopbackAddress()) {
                    return adr;
                }
                //instanceof Inet4Address vérifie que l'adresse est une IPv4.

                //!adr.isLoopbackAddress() élimine les adresses de boucle locale.

                //return adr renvoie la première adresse qui convient
            }
        }
        return InetAddress.getLocalHost();//Si aucune adresse n'a été trouvée, cette instruction tente de récupérer l'adresse associée au nom de la machine
    }
//réalise l'opération demandée
    static double calculer(int operation, int x) {
        switch (operation) {
            case addition:       return x + oper;
            case soustraction:   return x - oper;
            case multiplication: return (double) x * oper;
            case division:       return (double) x / oper;
            default:             return Double.NaN;
        }
    }
    public static void main(String[] args) {

        // La première étape :
//à compléter (TODO)
        try {

            final int PORT = 1234;
            ServerSocket socketServer = new ServerSocket(1234);

            System.out.println("Je suis un serveur en attente la connexion d'un client ");
// La deuxième étape :
            Socket socket = socketServer.accept();
            System.out.println("un client est connecté");
            //serveur en attente de ce nombre ,le recoit
            InputStream is=socket.getInputStream();
            DataInputStream dis=new DataInputStream(is);
            int nb= dis.read();
            System.out.println("le serveur a recu le nombre:"+nb);
            int x=nb*5;
            System.out.println("calcul du serveur:"+x+"*5"+x);
            OutputStream os=socket.getOutputStream();
            DataOutputStream dos=new DataOutputStream(os);
            dos.write(x);
            System.out.println("le resultat a ete  envoyeé au client:"+x);
            try {
                while (true) {
                    System.out.println("nombre reçu du client : " + x);

                    if (x == 0) {
                        System.out.println("0 reçu : fin de la communication");
                        break;
                    }

                    int resultat = x * 5;
                    System.out.println("calcul du serveur : " + x + " * 5 = " + resultat);
                    dos.writeInt(resultat);
                    try {
                        InetAddress adresse = (args.length >= 1)
                                ? InetAddress.getByName(args[0])
                                : trouverAdresseReelle();
                        int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 1234;
                        //try (ServerSocket serverSocket = new ServerSocket()) {
                        // serverSocket.bind(new InetSocketAddress(adresse, port));
                        //System.out.println("Serveur Écoute sur " + serverSocket.getLocalSocketAddress());
                        //System.out.println(" Le client doit se connecter à l'IP : "
                        //+ adresse.getHostAddress());
                        while (true) {
                            try (Socket socketd = socketServer.accept();
                                 DataInputStream in = new DataInputStream(socketd.getInputStream());
                                 DataOutputStream out = new DataOutputStream(socketd.getOutputStream())) {

                                System.out.println("Client connecté : "
                                        + socketd.getRemoteSocketAddress());

                                while (true) {
                                    int operation = in.readInt();
                                    if (operation == quitter) {
                                        System.out.println(" Le client a quitté");
                                        break;
                                    }
                                    int r = in.readInt();
                                    double res = calculer(operation, x);
                                    System.out.println("op=" + operation + ", x=" + r
                                            + " : " + res);
                                    out.writeDouble(res);
                                }
                            } catch (IOException e) {
                                System.out.println(" Connexion coupée par le client");
                            }
                        }
                    } finally {

                    }
                    //} catch (IOException | NumberFormatException e) {
                       //System.err.println(" Erreur : " + e.getMessage());
                   // }
                }
            } catch (EOFException e) {
                System.out.println("le client a fermé la connexion");
            } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
                dis.close();
                dos.close();
                socket.close();
                socketServer.close();
                System.out.println("sockets du serveur fermés");
            }




// La dernière étape : Fermer socket
// à compléter (TODO)
            // 3. Fermeture des sockets
            socket.close();
            socketServer.close();
            System.out.println(" Fin du programme.");
        }
        catch (IOException e)
        {
            System.out.println("erreur"+e.getMessage());
        }


    }
}
