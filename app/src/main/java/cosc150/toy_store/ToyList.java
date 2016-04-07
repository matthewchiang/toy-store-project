package cosc150.toy_store;


import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ToyList implements Parcelable {

    private ArrayList<Toy> toyList = new ArrayList<Toy>();

    public ToyList(){ }

    public ToyList(byte[] byteArray, int length) {
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);
        int cursor = 0;
        while (cursor < length) {
            int toyLength = buffer.getInt();

            byte[] toyBuffer = new byte[toyLength];
            buffer.get(toyBuffer, 0, toyLength);
            Toy toy = new Toy(toyBuffer);

            toyList.add(toy);
            cursor += Integer.SIZE / 8 + toyLength; //Integer.SIZE is # bits of int
        }
    }

    public void removeAllToys() {
        toyList.clear();
    }

    public void addToy(Toy toy) {
        toyList.add(toy);
    }

    public Toy getToy(int index) {
        return toyList.get(index);
    }

    public ArrayList<Toy> getToyList() {
        return toyList;
    }

    public int getNumOfToys() {
        return toyList.size();
    }

    public int getSizeInBytes() {
        int size = 0;
        for (int i = 0; i < toyList.size(); i++) {
            size += toyList.get(i).getSizeInBytes();
        }
        return size;
    }

    void putIntToByteArray(int number, ByteArrayOutputStream baos) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(Integer.SIZE / 8);
        b.putInt(number);
        baos.write(b.array());
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            for (int i = 0; i < toyList.size(); i++) {
                Toy toy = toyList.get(i);
                byte[] toyBuffer = toy.toByteArray();
                putIntToByteArray(toyBuffer.length, baos);
                baos.write(toyBuffer);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return baos.toByteArray();
    }

    public static void readFromFile(String filename) {
        try {
            RandomAccessFile file = new RandomAccessFile(filename, "r");
            int length = (int) file.length();
            byte[] temp = new byte[length];
            file.read(temp);
            file.close();
            ToyList toyList = new ToyList(temp, length);
            toyList.getNumOfToys();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle b = new Bundle();
        b.putParcelableArrayList("toyList", toyList);
        dest.writeBundle(b);
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public ToyList createFromParcel(Parcel in) {
            return new ToyList(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };

    public ToyList(Parcel in) {
        Bundle b = in.readBundle(Toy.class.getClassLoader());
        this.toyList = b.getParcelableArrayList("toyList");
    }
}