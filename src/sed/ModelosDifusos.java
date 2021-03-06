package sed;

import MD.ModeloDifuso;
import java.io.*;
import java.util.Scanner;
/**
 *
 * @author Ramón
 */

//Escribe toda la información de cada modelo difuso en el archivo "modelos difusos"
public class ModelosDifusos {
    int llave;
    //String nom_mod,nom_eti,tipo_fun;
    //double inicio,fin,arg;
    double auxi1,auxi2;
    public void escribir_Modelo (String nom_mod, double inicio, double fin, int num_eti) throws IOException{
        double [] puntos =new double[2];
        int n=1,aux=num_eti,ind;
        if(num_eti>5)
            num_eti=5;
        String nom_eti,tipo_fun;
        StringBuffer buffer=null;
        System.out.println("");
        RandomAccessFile archi=new RandomAccessFile("modelos_difusos","rw");
        long pos=archi.length();
        archi.seek(pos);
        System.out.println(pos);
        ind=escribe_Indice(pos);
        Scanner entrada=new Scanner(System.in);
        archi.writeInt(ind);
        buffer=new StringBuffer(nom_mod);
        buffer.setLength(20);
        archi.writeChars(buffer.toString());
        archi.writeDouble(inicio);
        archi.writeDouble(fin);
        do{
            double arg1=0,arg2=0,arg3=0,arg4=0;
            System.out.println("Ingresa el nombre de la etiqueta "+n);
            nom_eti=entrada.next();
            buffer=new StringBuffer(nom_eti);
            buffer.setLength(15);
            archi.writeChars(buffer.toString());
            System.out.println("Ingresa el tipo de función (triangular=tri, trapezoidal=tra, otra)");
            tipo_fun=entrada.next();
            buffer=new StringBuffer(tipo_fun);
            buffer.setLength(5);
            archi.writeChars(buffer.toString());
            switch(tipo_fun){
                case "tri":
                    System.out.println("Ingresa el valor en X del punto máximo de la función");
                    arg1=entrada.nextDouble();
                    puntos=calcula("tri", arg1,arg2,n,inicio,fin);
                    break;
                case "tra":
                    System.out.println("Ingresa el valor en X del primer punto máximo de la función");
                    arg1=entrada.nextDouble();
                    System.out.println("Ingresa el valor en X del segundo punto máximo de la función");
                    arg2=entrada.nextDouble();
                    puntos=calcula("tra",arg1,arg2,n,inicio,fin);
                    break;
                case "otra":
                    break;
            }
            archi.writeDouble(arg1);
            archi.writeDouble(arg2);
            archi.writeDouble(arg3);
            archi.writeDouble(arg4);
            archi.writeDouble(puntos[0]);
            archi.writeDouble(puntos[1]);
            num_eti--;
            n++;
        }while (num_eti!=0);
        if(aux<5){
            for(int i=5-aux;i>0;i--)
            {
                buffer=new StringBuffer("");
                buffer.setLength(15);
                archi.writeChars(buffer.toString());
                buffer=new StringBuffer("");
                buffer.setLength(5);
                archi.writeChars(buffer.toString());
                archi.writeDouble(0);
                archi.writeDouble(0);
                archi.writeDouble(0);
                archi.writeDouble(0);
                archi.writeDouble(0);
                archi.writeDouble(0);
            }
        }
        archi.close();
    }
    
    //Método para calcular el inicio de una función y su fin
    public double [] calcula(String tipo, double arg1, double arg2, int aux, double inicio,double fin){
        double[] res=new double[2];
        double tras=0.45;
        if(aux==1){                                     //Cuando se calcula el fin de la primer función
            if("tri".equals(tipo)){                            //Si la función es de tipo triangular
                if(arg1==inicio){                       //Si el punto crítico de la función es cero
                    res[0]=inicio;
                    res[1]=(fin-inicio)*1.20;
                    auxi1=inicio;
                    auxi2=res[1];
                }
                else{                                   //Si el punto crítico de la función es diferente de cero
                    res[0]=inicio;
                    res[1]=2*arg1-inicio;
                    auxi1=inicio;
                    auxi2=res[1];
                }
            }
            else{                                       //Si la función es trapezoidal
                if(arg1==inicio){                       //Si el primer punto crítico de la función es cero
                    res[0]=inicio;
                    res[1]=(fin-inicio)*1.20;
                    auxi1=inicio;
                    auxi2=res[1];
                }
                else{                                   //Si el primer punto de la función es diferente de cero
                    res[0]=inicio;
                    res[1]=arg1-inicio+arg2;
                    auxi1=inicio;
                    auxi2=res[1];
                }
            }
        }
        else{                                           //Cuando se calcula una función que no sea la primera
            if("tri".equals(tipo)){                            //Si la función es triangular
                res[0]=auxi2-((auxi2-auxi1)/2)*tras;
                res[1]=2*arg1-res[0];
                auxi1=res[0];
                auxi2=res[1];
            }
            else{                                       //Si la función es trapezoidal
                res[0]=auxi2-((auxi2-auxi1)/2)*tras;
                res[1]=arg1-res[0]+arg2;
                auxi1=res[0];
                auxi2=res[1];
            }
        }
        System.out.println("Inicio: "+auxi1);
        System.out.println("Fin: "+auxi2);
        return res;
    }
    
    //Escribir en el índice
    public int escribe_Indice (long pos) throws IOException{    
        int llave;
        RandomAccessFile indice=new RandomAccessFile("indice", "rw");
        long posi=indice.length(),aux=posi;
        if(posi==0)
            llave=1;
        else{
            posi-=12;
            indice.seek(posi);
            llave=indice.readInt()+1;
        }
        indice.seek(aux);
        indice.writeInt(llave);
        indice.writeLong(pos);
        indice.close();
        return llave;
    }
    
    //Leer el índice
    public void leer_Indice() throws IOException{
        long ap_actual,ap_final;
        RandomAccessFile archi=new RandomAccessFile("indice", "r");
        while((ap_actual=archi.getFilePointer())!=(ap_final=archi.length())){
            System.out.println(archi.readInt());
            System.out.println("-"+archi.readLong()+"\n");
        }
        archi.close();
    }
    
    //Leer el modelo
    public void leer_Modelo () throws IOException{
        long ap_actual,ap_final;
        RandomAccessFile archi=new RandomAccessFile("modelos_difusos", "r");
        while((ap_actual=archi.getFilePointer())!=(ap_final=archi.length())){
            char nom_mod[]=new char[20],nom_eti[]=new char[15],tipo_fun[]=new char[5],temp;
            archi.readInt();
            String nom_m="",nom_e,tipo_f;
            for(int c=0;c<nom_mod.length;c++){
                temp=archi.readChar();
                //nom_mod[c]=temp;
                nom_m+=temp;
            }
            //new String(nom_mod).replace('\0',' ');
            System.out.println("Variable: "+nom_m);
            System.out.println("Universo de Discurso: "+archi.readDouble()+" - "+archi.readDouble());
            for(int i=0;i<5;i++){
                nom_e=""; tipo_f="";
                for(int c=0;c<nom_eti.length;c++){
                    temp=archi.readChar();
                    //nom_eti[c]=temp;
                    nom_e+=temp;
                }
                //new String(nom_eti).replace('\0',' ');
                System.out.println("Etiqueta: "+nom_e);
                for(int c=0;c<tipo_fun.length;c++){
                    temp=archi.readChar();
                    tipo_f+=temp;
                }
                //new String(tipo_fun).replace('\0', ' ');
                System.out.println("Tipo función: "+tipo_f);
                for(int j=0;j<4;j++){
                    System.out.println("Argumento "+j+": "+archi.readDouble());
                }
                System.out.println("Inicio: "+archi.readDouble());
                System.out.println("Fin: "+archi.readDouble());
            }
            
        }
        archi.close();
    }

    public ModeloDifuso buscarModelo(String string) throws FileNotFoundException, IOException 
    {
        System.out.println("Buscando: "+string);
        long ap_actual,ap_final;
        RandomAccessFile archi=new RandomAccessFile("indice", "r");
        modelo:
        while((ap_actual=archi.getFilePointer())!=(ap_final=archi.length()))
        {
            int llave = archi.readInt();
            long posicion = archi.readLong();
            RandomAccessFile archim=new RandomAccessFile("modelos_difusos","rw");
            archim.seek(posicion);
            archim.readInt();            
            String temp = "";
            for (int i = 0; i < string.length(); i++) 
            {
                char readChar = archim.readChar();
                temp += readChar;
                if(readChar != string.charAt(i))
                {
                    System.out.println("Caracter distinto:"+string.charAt(i)+"("+temp+")");
                    for (int j = 0; j < 20 - i; j++) 
                    {
                        archim.readChar();
                    }
                    archim.close();
                    continue modelo;
                }                
            }
            System.out.println("Modelo encontrado: "+string);
            archi.close();
            return new ModeloDifuso(archim,posicion);
            
        }
        archi.close();
        System.out.println("Modelo no encontrado: "+string+"\nVerifique que escribió bien el nombre o si la variable existe(ver modelos)");
        return null;
    }
 }