package org.kamranzafar.jtar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class TarOutputStream extends OutputStream {
    private long bytesWritten;
    private TarEntry currentEntry;
    private long currentFileSize;
    private final OutputStream out;

    public TarOutputStream(OutputStream out2) {
        this.out = out2;
        this.bytesWritten = 0;
        this.currentFileSize = 0;
    }

    public TarOutputStream(File fout) throws FileNotFoundException {
        this.out = new BufferedOutputStream(new FileOutputStream(fout));
        this.bytesWritten = 0;
        this.currentFileSize = 0;
    }

    public TarOutputStream(File fout, boolean append) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fout, "rw");
        long fileSize = fout.length();
        if (append && fileSize > 1024) {
            raf.seek(fileSize - 1024);
        }
        this.out = new BufferedOutputStream(new FileOutputStream(raf.getFD()));
    }

    public void close() throws IOException {
        closeCurrentEntry();
        write(new byte[TarConstants.EOF_BLOCK]);
        this.out.close();
    }

    public void write(int b) throws IOException {
        this.out.write(b);
        this.bytesWritten++;
        if (this.currentEntry != null) {
            this.currentFileSize++;
        }
    }

    public void write(byte[] b, int off, int len) throws IOException {
        TarEntry tarEntry = this.currentEntry;
        if (tarEntry == null || tarEntry.isDirectory() || this.currentEntry.getSize() >= this.currentFileSize + ((long) len)) {
            this.out.write(b, off, len);
            this.bytesWritten += (long) len;
            if (this.currentEntry != null) {
                this.currentFileSize += (long) len;
                return;
            }
            return;
        }
        throw new IOException("The current entry[" + this.currentEntry.getName() + "] size[" + this.currentEntry.getSize() + "] is smaller than the bytes[" + (this.currentFileSize + ((long) len)) + "] being written.");
    }

    public void putNextEntry(TarEntry entry) throws IOException {
        closeCurrentEntry();
        byte[] header = new byte[512];
        entry.writeEntryHeader(header);
        write(header);
        this.currentEntry = entry;
    }

    /* access modifiers changed from: protected */
    public void closeCurrentEntry() throws IOException {
        TarEntry tarEntry = this.currentEntry;
        if (tarEntry == null) {
            return;
        }
        if (tarEntry.getSize() <= this.currentFileSize) {
            this.currentEntry = null;
            this.currentFileSize = 0;
            pad();
            return;
        }
        throw new IOException("The current entry[" + this.currentEntry.getName() + "] of size[" + this.currentEntry.getSize() + "] has not been fully written.");
    }

    /* access modifiers changed from: protected */
    public void pad() throws IOException {
        int extra;
        long j = this.bytesWritten;
        if (j > 0 && (extra = (int) (j % 512)) > 0) {
            write(new byte[(512 - extra)]);
        }
    }
}
