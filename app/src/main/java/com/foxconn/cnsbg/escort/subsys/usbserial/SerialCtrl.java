package com.foxconn.cnsbg.escort.subsys.usbserial;

import android.content.Context;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

public class SerialCtrl {
    public static final int BAUD_RATE_300 = 300;
    public static final int BAUD_RATE_600 = 600;
    public static final int BAUD_RATE_1200 = 1200;
    public static final int BAUD_RATE_4800 = 4800;
    public static final int BAUD_RATE_9600 = 9600;
    public static final int BAUD_RATE_19200 = 19200;
    public static final int BAUD_RATE_38400 = 38400;
    public static final int BAUD_RATE_57600 = 57600;
    public static final int BAUD_RATE_115200 = 115200;
    public static final int BAUD_RATE_230400 = 230400;
    public static final int BAUD_RATE_460800 = 460800;
    public static final int BAUD_RATE_921600 = 921600;

    public static final byte DATA_BITS_5 = 5;
    public static final byte DATA_BITS_6 = 6;
    public static final byte DATA_BITS_7 = 7;
    public static final byte DATA_BITS_8 = 8;

    public static final byte STOP_BITS_1 = 1;
    public static final byte STOP_BITS_1_5 = 3;
    public static final byte STOP_BITS_2 = 2;

    public static final byte PARITY_NONE = 0;
    public static final byte PARITY_ODD = 1;
    public static final byte PARITY_EVEN = 2;
    public static final byte PARITY_MARK = 3;
    public static final byte PARITY_SPACE = 4;

    public static final byte FLOW_CONTROL_NONE = 0;
    public static final byte FLOW_CONTROL_RTSCTS_IN = 1;
    public static final byte FLOW_CONTROL_RTSCTS_OUT = 2;
    public static final byte FLOW_CONTROL_XONXOFF_IN = 4;
    public static final byte FLOW_CONTROL_XONXOFF_OUT = 8;

    private FT311UARTInterface uart;
    //private UsbSerialInterface uart;
    //private Physicaloid uart;

    public SerialCtrl(Context context) {
        uart = new FT311UARTInterface(context);
        //uart = new UsbSerialInterface(context);
        //uart = new Physicaloid(context);
    }

    public int open() {
        return UsbSerialOpen(uart);
    }

    public void close() {
        UsbSerialClose(uart);
    }

    public int read(byte[] buffer, int size) {
        return UsbSerialRead(uart, buffer, size);
    }

    public void write(String destStr) {
        UsbSerialWrite(uart, destStr);
    }

    public void config(int baudRate, byte dataBit, byte stopBit, byte parity, byte flowCtrl) {
        UsbSerialConfig(uart, baudRate, dataBit, stopBit, parity, flowCtrl);
    }

    /**********************************************************************************************/

    private int UsbSerialOpen(FT311UARTInterface uart) {
        return uart.ResumeAccessory();
    }

    private void UsbSerialClose(FT311UARTInterface uart) {
        uart.DestroyAccessory(true);
    }

    private int UsbSerialRead(FT311UARTInterface uart, byte[] buffer, int size) {
        int[] readBytes = new int[1];
        byte status = uart.ReadData(size, buffer, readBytes);
        if (status == 0x00 && readBytes[0] > 0) {
            return readBytes[0];
        }

        return 0;
    }

    private void UsbSerialWrite(FT311UARTInterface uart, String destStr) {
        uart.SendData(destStr.length(), destStr.getBytes());
    }

    private void UsbSerialConfig(FT311UARTInterface uart, int baudRate, byte dataBit, byte stopBit,
                                 byte parity, byte flowCtrl) {
        uart.SetConfig(baudRate, dataBit, stopBit, parity, flowCtrl);
    }

    /**********************************************************************************************/

    private int UsbSerialOpen(UsbSerialInterface userial) {
        return userial.ResumeAccessory();
    }

    private void UsbSerialClose(UsbSerialInterface userial) {
        userial.DestroyAccessory(true);
    }

    private int UsbSerialRead(UsbSerialInterface userial, byte[] buffer, int size) {
        int[] readBytes = new int[1];
        byte status = userial.ReadData(size, buffer, readBytes);
        if (status == 0x00 && readBytes[0] > 0) {
            return readBytes[0];
        }

        return 0;
    }

    private void UsbSerialWrite(UsbSerialInterface userial, String destStr) {
        userial.SendData(destStr.length(), destStr.getBytes());
    }

    private void UsbSerialConfig(UsbSerialInterface userial, int baudRate,
                                 byte dataBit, byte stopBit, byte parity, byte flowCtrl) {
        userial.SetConfig(baudRate, dataBit, stopBit, parity, flowCtrl);
    }

    /**********************************************************************************************/

    private int UsbSerialOpen(Physicaloid physicaloid) {
        try {
            if (physicaloid.isOpened())
                return 1;

            if (physicaloid.open())
                return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 2;
    }

    private void UsbSerialClose(Physicaloid physicaloid) {
        try {
            physicaloid.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int UsbSerialRead(Physicaloid physicaloid, byte[] buffer, int size) {
        int readBytes;

        try {
            readBytes = physicaloid.read(buffer, size);
        } catch (Exception e) {
            e.printStackTrace();
            readBytes = 0;
        }

        return readBytes;
    }

    private void UsbSerialWrite(Physicaloid physicaloid, String destStr) {
        try {
            physicaloid.write(destStr.getBytes(), destStr.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void UsbSerialConfig(Physicaloid physicaloid, int baudRate, byte dataBit,
                                   byte stopBit, byte parity, byte flowCtrl) {
        try {
            UartConfig config = new UartConfig(baudRate, dataBit, stopBit, parity, false, false);
            physicaloid.setConfig(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
