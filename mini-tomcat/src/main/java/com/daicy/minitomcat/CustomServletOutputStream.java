package com.daicy.minitomcat;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

// 自定义的类似ServletOutputStream的类
public class CustomServletOutputStream extends ServletOutputStream {

    private final OutputStream outputStream;

    public CustomServletOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
    }

    @Override
    public boolean isReady() {
        // 这里可以根据实际情况返回是否准备好输出数据的状态，简单起见返回true
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }

}