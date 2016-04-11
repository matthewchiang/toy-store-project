package cosc150.toy_store;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Toy implements Parcelable {
    String toyName = null;
    Bitmap image = null;
    int price = 0;

    public Toy() {
    }

    public Toy(String toyName, int price, Bitmap image) {
        this.toyName = toyName;
        this.image = image;
        this.price = price;
    }

    public Toy(byte[] byteArray) {
        ByteBuffer buffer = ByteBuffer.wrap(byteArray);

        int nameLength = buffer.getInt();
        byte[] nameBuffer = new byte[nameLength];
        buffer.get(nameBuffer, 0, nameLength);
        this.toyName = new String(nameBuffer);

        this.price = buffer.getInt();

        int imageLength = buffer.getInt();
        byte[] imageBuffer = new byte[imageLength];
        buffer.get(imageBuffer, 0, imageLength);
        Bitmap tmp = BitmapFactory.decodeByteArray(imageBuffer, 0, imageLength);
        this.image = Bitmap.createScaledBitmap(tmp, 180, 180, false);

        Log.d("print", "created a toy");

    }

    static Toy getToyInfo(byte[] byteArray) {
        Toy toy = new Toy(byteArray);
        return toy;
    }

    public int getSizeInBytes() {
        int size = 0;
        size += Integer.SIZE / 8 + toyName.length();
        size += Integer.SIZE / 8;
        size += Integer.SIZE / 8 + getImageSize();

        return size;
    }

    public String getToyName() {
        return toyName;
    }

    public int getPrice() {
        return price;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getImageSize() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //ImageIO.write(image, "jpg", baos);
        return baos.toByteArray().length;
    }

    void putIntToByteArray(int number, ByteArrayOutputStream baos) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(Integer.SIZE / 8);
        b.putInt(number);
        baos.write(b.array());
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            putIntToByteArray(toyName.length(), baos);
            baos.write(toyName.getBytes());
            putIntToByteArray(price, baos);
            putIntToByteArray(getImageSize(), baos);
            //ImageIO.write(image, "jpg", baos);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return baos.toByteArray();
    }

    public Toy(Parcel p) {

        this.toyName = p.readString();

        //convert int to bytearray to bitmap
        byte[] bitmapdata = new byte[p.readInt()];

        p.readByteArray(bitmapdata);
        Bitmap bmap = BitmapFactory.decodeByteArray(bitmapdata , 0, bitmapdata.length);
        this.image = bmap;

        this.price = p.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(toyName);

        //Convert bitmap back to bytearray

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] imgByteArray = stream.toByteArray();
            stream.close();

            dest.writeInt(imgByteArray.length);
            dest.writeByteArray(imgByteArray);

        } catch (IOException e) {
            e.printStackTrace();
        }

        dest.writeInt(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public Toy createFromParcel(Parcel in) {
            return new Toy(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[size];
        }
    };
}