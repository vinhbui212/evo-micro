package org.example.thuan_security.response;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HttpResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream outputStream;
    private final ServletOutputStream servletOutputStream;

    public HttpResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        outputStream = new ByteArrayOutputStream();
        servletOutputStream = new WrappedServletOutputStream(outputStream);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override
    public void flushBuffer() throws IOException {
        super.flushBuffer();
        outputStream.writeTo(getResponse().getOutputStream()); // Copy data to real response output stream
    }

    public String getResponseBody() {
        return outputStream.toString(); // Return response body from the ByteArrayOutputStream
    }

    private static class WrappedServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream outputStream;

        public WrappedServletOutputStream(ByteArrayOutputStream outputStream) {
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
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }
}
