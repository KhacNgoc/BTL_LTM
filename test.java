
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author khacngoc
 */
public class test {
    public static final int SIZE_BUFF = 4096;
    public static final int NUM_SPLIT = 3;
    public static final String PATH = "/home/khacngoc/Documents/NetworkProgramer/splitFile/";
    public static void main(String[] args) throws IOException {
           splitFile(PATH+"test.mp4");
           mergeFile(PATH+"split1", PATH+"split2", PATH+"split3", PATH+"result.mp4");
    }
    public static void splitFile(String fileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "r");
        long numSplits = NUM_SPLIT;
        long sourceSize = raf.length();
        long bytesPerSplit = sourceSize/numSplits;
        long remainingBytes = sourceSize % numSplits;
        int maxReadBufferSize = SIZE_BUFF;
        for(int destIx=1; destIx <= numSplits; destIx++) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(PATH+"split"+destIx));
            if(bytesPerSplit > maxReadBufferSize) {
                long numReads = bytesPerSplit/maxReadBufferSize;
                long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                for(int i=0; i<numReads; i++) {
                    readWrite(raf, bw, maxReadBufferSize);
                }
                if(numRemainingRead > 0) {
                    readWrite(raf, bw, numRemainingRead);
                }
            }else {
                readWrite(raf, bw, bytesPerSplit);
            }
            bw.close();
        }
        if(remainingBytes > 0) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(PATH+"split"+(numSplits+1)));
            readWrite(raf, bw, remainingBytes);
            bw.close();
            IOCopier.joinFiles(new File(PATH+"split5"), new File[] {
                new File(PATH+"split3"), new File(PATH+"split4")});
            File f1 = new File(PATH+"split3");
            boolean b = f1.delete();
            System.out.println("Delete file 1 remaining success.");
            File f2 = new File(PATH+"split4");
            boolean c = f2.delete();
            System.out.println("Delete file 2 remaining success.");
            File f3 = new File(PATH+"split5");
            File f4 = new File(PATH+"split3");
            boolean d = f3.renameTo(f4);
            f3.delete();
        }
        raf.close();
    }
    static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if(val != -1) {
            bw.write(buf);
        }
    }
    public static void mergeFile(String file1, String file2, String file3, String result) throws IOException{
        IOCopier.joinFiles(new File(result), new File[] {
                new File(file1), new File(file2), new File(file3)});
        File f1 = new File(file1);
        boolean b = f1.delete();
        File f2 = new File(file2);
        boolean c = f2.delete();
        File f3 = new File(file3);
        boolean d = f3.delete();
    }
}
