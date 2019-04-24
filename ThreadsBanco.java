/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Threads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gabriel
 */
public class ThreadsBanco {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        Banco b = new Banco();
        for (int i = 0; i < 100; i++) {

            EjecucionTransferencia r = new EjecucionTransferencia(b, i, 2000);
            Thread t = new Thread(r);
            t.start();
        }
    }

}

class Banco {

    private final double[] cuentas;
    private Lock cierre_banco = new ReentrantLock();
    private Condition saldo_suficiente;

    public Banco() {

        cuentas = new double[100];

        for (int i = 0; i < cuentas.length; i++) {
            cuentas[i] = 2000;
        }
        saldo_suficiente=cierre_banco.newCondition();
        
        
    }

    public void Transferencia(int cuenta_origen, int cuenta_destino, double cantidad) throws InterruptedException {
        cierre_banco.lock();
        try{
        //Checa si la cuenta cuenta con saldo suficiente para transferir
       /*
        if (cuentas[cuenta_origen] < cantidad) {
            System.out.println("Saldo insuficiente: "+cuentas[cuenta_origen]+"..."+cantidad);
            return;
        }
        else {
            System.out.println("Transaccion verificada: "+ cuentas[cuenta_origen]);
        }*/
       
       while (cuentas[cuenta_origen] < cantidad){
           
           saldo_suficiente.await();
       }
        System.out.println(Thread.currentThread());
        //Dinero que se descontara de la cuenta
        cuentas[cuenta_origen] -= cantidad;
        System.out.printf("%10.2f de cuenta %d para cuenta %d ", cantidad, cuenta_origen, cuenta_destino);
        cuentas[cuenta_destino] += cantidad;
        System.out.printf("Saldo total %10.2f%n", getSaldoT());
        saldo_suficiente.signalAll();
        
    }finally{
            cierre_banco.unlock();
        }
    }

    public double getSaldoT() {

        double suma_cuentas = 0;
        for (double a : cuentas) {

            suma_cuentas += a;
        }
        return suma_cuentas;

    }

}

class EjecucionTransferencia implements Runnable {

    private Banco banco;
    private int de_la_cuenta;
    private double cantidad_maxima;

    public EjecucionTransferencia(Banco b, int de, double max) {

        banco = b;
        de_la_cuenta = de;
        cantidad_maxima = max;

    }

    @Override
    public void run() {

        while (true) {

            try {
                int para_la_cuenta = (int) (100 * Math.random());
                double cantidad = cantidad_maxima * Math.random();
                banco.Transferencia(de_la_cuenta, para_la_cuenta, cantidad);
                Thread.sleep((int) (10 * Math.random()));
            } catch (InterruptedException ex) {
                Logger.getLogger(EjecucionTransferencia.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
