package org.kamranzafar.jtar;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TarInputStream extends FilterInputStream {
    private static final int SKIP_BUFFER_SIZE = 2048;
    private long bytesRead = 0;
    private TarEntry currentEntry;
    private long currentFileSize = 0;
    private boolean defaultSkip = false;

    public TarInputStream(InputStream in) {
        super(in);
    }

    public boolean markSupported() {
        return false;
    }

    public synchronized void mark(int readlimit) {
    }

    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    public int read() throws IOException {
        byte[] buf = new byte[1];
        int res = read(buf, 0, 1);
        if (res != -1) {
            return buf[0] & 255;
        }
        return res;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        TarEntry tarEntry = this.currentEntry;
        if (tarEntry != null) {
            if (this.currentFileSize == tarEntry.getSize()) {
                return -1;
            }
            if (this.currentEntry.getSize() - this.currentFileSize < ((long) len)) {
                len = (int) (this.currentEntry.getSize() - this.currentFileSize);
            }
        }
        int br = super.read(b, off, len);
        if (br != -1) {
            if (this.currentEntry != null) {
                this.currentFileSize += (long) br;
            }
            this.bytesRead += (long) br;
        }
        return br;
    }

    public TarEntry getNextEntry() throws IOException {
        int i;
        int res;
        closeCurrentEntry();
        byte[] header = new byte[512];
        byte[] theader = new byte[512];
        int tr = 0;
        while (true) {
            i = 0;
            if (tr >= 512 || (res = read(theader, 0, 512 - tr)) < 0) {
                boolean eof = true;
                int length = header.length;
            } else {
                System.arraycopy(theader, 0, header, tr, res);
                tr += res;
            }
        }
        boolean eof2 = true;
        int length2 = header.length;
        while (true) {
            if (i >= length2) {
                break;
            } else if (header[i] != 0) {
                eof2 = false;
                break;
            } else {
                i++;
            }
        }
        if (!eof2) {
            this.currentEntry = new TarEntry(header);
        }
        return this.currentEntry;
    }

    public long getCurrentOffset() {
        return this.bytesRead;
    }

    /* access modifiers changed from: protected */
    public void closeCurrentEntry() throws IOException {
        TarEntry tarEntry = this.currentEntry;
        if (tarEntry != null) {
            if (tarEntry.getSize() > this.currentFileSize) {
                long bs = 0;
                while (bs < this.currentEntry.getSize() - this.currentFileSize) {
                    long res = skip((this.currentEntry.getSize() - this.currentFileSize) - bs);
                    if (res != 0 || this.currentEntry.getSize() - this.currentFileSize <= 0) {
                        bs += res;
                    } else {
                        throw new IOException("Possible tar file corruption");
                    }
                }
            }
            this.currentEntry = null;
            this.currentFileSize = 0;
            skipPad();
        }
    }

    /* access modifiers changed from: protected */
    public void skipPad() throws IOException {
        int extra;
        long j = this.bytesRead;
        if (j > 0 && (extra = (int) (j % 512)) > 0) {
            long bs = 0;
            while (bs < ((long) (512 - extra))) {
                bs += skip(((long) (512 - extra)) - bs);
            }
        }
    }

    public long skip(long n) throws IOException {
        if (this.defaultSkip) {
            long bs = super.skip(n);
            this.bytesRead += bs;
            return bs;
        } else if (n <= 0) {
            return 0;
        } else {
            long left = n;
            byte[] sBuff = new byte[SKIP_BUFFER_SIZE];
            while (left > 0) {
                long j = 2048;
                if (left < 2048) {
                    j = left;
                }
                int res = read(sBuff, 0, (int) j);
                if (res < 0) {
                    break;
                }
                left -= (long) res;
            }
            return n - left;
        }
    }

    public boolean isDefaultSkip() {
        return this.defaultSkip;
    }

    public void setDefaultSkip(boolean defaultSkip2) {
        this.defaultSkip = defaultSkip2;
    }
}
