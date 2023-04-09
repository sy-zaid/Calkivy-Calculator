package org.libsdl.app;

import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

public class SDLAudioManager {
    protected static final String TAG = "SDLAudio";
    protected static AudioRecord mAudioRecord;
    protected static AudioTrack mAudioTrack;

    public static native int nativeSetupJNI();

    public static void initialize() {
        mAudioTrack = null;
        mAudioRecord = null;
    }

    protected static String getAudioFormatString(int audioFormat) {
        switch (audioFormat) {
            case 2:
                return "16-bit";
            case 3:
                return "8-bit";
            case 4:
                return "float";
            default:
                return Integer.toString(audioFormat);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00a6  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00d7  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0155  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x01ad  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0209  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static int[] open(boolean r28, int r29, int r30, int r31, int r32) {
        /*
            r0 = r29
            r1 = r31
            r2 = r32
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Opening "
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r5 = "capture"
            java.lang.String r6 = "playback"
            if (r28 == 0) goto L_0x0019
            r7 = r5
            goto L_0x001a
        L_0x0019:
            r7 = r6
        L_0x001a:
            java.lang.StringBuilder r3 = r3.append(r7)
            java.lang.String r7 = ", requested "
            java.lang.StringBuilder r3 = r3.append(r7)
            java.lang.StringBuilder r3 = r3.append(r2)
            java.lang.String r7 = " frames of "
            java.lang.StringBuilder r3 = r3.append(r7)
            java.lang.StringBuilder r3 = r3.append(r1)
            java.lang.String r8 = " channel "
            java.lang.StringBuilder r3 = r3.append(r8)
            java.lang.String r9 = getAudioFormatString(r30)
            java.lang.StringBuilder r3 = r3.append(r9)
            java.lang.String r9 = " audio at "
            java.lang.StringBuilder r3 = r3.append(r9)
            java.lang.StringBuilder r3 = r3.append(r0)
            java.lang.String r10 = " Hz"
            java.lang.StringBuilder r3 = r3.append(r10)
            java.lang.String r3 = r3.toString()
            java.lang.String r11 = "SDLAudio"
            android.util.Log.v(r11, r3)
            int r3 = android.os.Build.VERSION.SDK_INT
            r12 = 21
            r13 = 2
            if (r3 >= r12) goto L_0x0072
            if (r1 <= r13) goto L_0x0063
            r1 = 2
        L_0x0063:
            r3 = 8000(0x1f40, float:1.121E-41)
            if (r0 >= r3) goto L_0x006a
            r0 = 8000(0x1f40, float:1.121E-41)
            goto L_0x0072
        L_0x006a:
            r3 = 48000(0xbb80, float:6.7262E-41)
            if (r0 <= r3) goto L_0x0072
            r0 = 48000(0xbb80, float:6.7262E-41)
        L_0x0072:
            r3 = 23
            r14 = 4
            r15 = r30
            if (r15 != r14) goto L_0x0083
            if (r28 == 0) goto L_0x007d
            r12 = 23
        L_0x007d:
            int r13 = android.os.Build.VERSION.SDK_INT
            if (r13 >= r12) goto L_0x0083
            r13 = 2
            goto L_0x0084
        L_0x0083:
            r13 = r15
        L_0x0084:
            switch(r13) {
                case 2: goto L_0x00aa;
                case 3: goto L_0x00a8;
                case 4: goto L_0x00a6;
                default: goto L_0x0087;
            }
        L_0x0087:
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r15 = "Requested format "
            java.lang.StringBuilder r12 = r12.append(r15)
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r15 = ", getting ENCODING_PCM_16BIT"
            java.lang.StringBuilder r12 = r12.append(r15)
            java.lang.String r12 = r12.toString()
            android.util.Log.v(r11, r12)
            r13 = 2
            r12 = 2
            goto L_0x00ac
        L_0x00a6:
            r12 = 4
            goto L_0x00ac
        L_0x00a8:
            r12 = 1
            goto L_0x00ac
        L_0x00aa:
            r12 = 2
        L_0x00ac:
            java.lang.String r15 = " channels, getting stereo"
            java.lang.String r14 = "Requested "
            if (r28 == 0) goto L_0x00d7
            switch(r1) {
                case 1: goto L_0x00d4;
                case 2: goto L_0x00d1;
                default: goto L_0x00b5;
            }
        L_0x00b5:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.StringBuilder r3 = r3.append(r14)
            java.lang.StringBuilder r3 = r3.append(r1)
            java.lang.StringBuilder r3 = r3.append(r15)
            java.lang.String r3 = r3.toString()
            android.util.Log.v(r11, r3)
            r1 = 2
            r3 = 12
            goto L_0x012f
        L_0x00d1:
            r3 = 12
            goto L_0x012f
        L_0x00d4:
            r3 = 16
            goto L_0x012f
        L_0x00d7:
            switch(r1) {
                case 1: goto L_0x012d;
                case 2: goto L_0x012a;
                case 3: goto L_0x0127;
                case 4: goto L_0x0124;
                case 5: goto L_0x0121;
                case 6: goto L_0x011e;
                case 7: goto L_0x011b;
                case 8: goto L_0x00f6;
                default: goto L_0x00da;
            }
        L_0x00da:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.StringBuilder r3 = r3.append(r14)
            java.lang.StringBuilder r3 = r3.append(r1)
            java.lang.StringBuilder r3 = r3.append(r15)
            java.lang.String r3 = r3.toString()
            android.util.Log.v(r11, r3)
            r1 = 2
            r3 = 12
            goto L_0x012f
        L_0x00f6:
            int r15 = android.os.Build.VERSION.SDK_INT
            if (r15 < r3) goto L_0x00fd
            r3 = 6396(0x18fc, float:8.963E-42)
            goto L_0x012f
        L_0x00fd:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.StringBuilder r3 = r3.append(r14)
            java.lang.StringBuilder r3 = r3.append(r1)
            java.lang.String r14 = " channels, getting 5.1 surround"
            java.lang.StringBuilder r3 = r3.append(r14)
            java.lang.String r3 = r3.toString()
            android.util.Log.v(r11, r3)
            r1 = 6
            r3 = 252(0xfc, float:3.53E-43)
            goto L_0x012f
        L_0x011b:
            r3 = 1276(0x4fc, float:1.788E-42)
            goto L_0x012f
        L_0x011e:
            r3 = 252(0xfc, float:3.53E-43)
            goto L_0x012f
        L_0x0121:
            r3 = 220(0xdc, float:3.08E-43)
            goto L_0x012f
        L_0x0124:
            r3 = 204(0xcc, float:2.86E-43)
            goto L_0x012f
        L_0x0127:
            r3 = 28
            goto L_0x012f
        L_0x012a:
            r3 = 12
            goto L_0x012f
        L_0x012d:
            r3 = 4
        L_0x012f:
            int r21 = r12 * r1
            if (r28 == 0) goto L_0x013a
            int r14 = android.media.AudioRecord.getMinBufferSize(r0, r3, r13)
            r22 = r14
            goto L_0x0140
        L_0x013a:
            int r14 = android.media.AudioTrack.getMinBufferSize(r0, r3, r13)
            r22 = r14
        L_0x0140:
            int r14 = r22 + r21
            r15 = 1
            int r14 = r14 - r15
            int r14 = r14 / r21
            int r2 = java.lang.Math.max(r2, r14)
            r14 = 4
            int[] r14 = new int[r14]
            r23 = 3
            r24 = 0
            r25 = 0
            if (r28 == 0) goto L_0x01ad
            android.media.AudioRecord r16 = mAudioRecord
            if (r16 != 0) goto L_0x018c
            android.media.AudioRecord r20 = new android.media.AudioRecord
            r16 = 0
            int r19 = r2 * r21
            r26 = r14
            r14 = r20
            r29 = r1
            r1 = 1
            r15 = r16
            r16 = r0
            r17 = r3
            r18 = r13
            r14.<init>(r15, r16, r17, r18, r19)
            mAudioRecord = r20
            int r14 = r20.getState()
            if (r14 == r1) goto L_0x0186
            java.lang.String r1 = "Failed during initialization of AudioRecord"
            android.util.Log.e(r11, r1)
            android.media.AudioRecord r1 = mAudioRecord
            r1.release()
            mAudioRecord = r25
            return r25
        L_0x0186:
            android.media.AudioRecord r14 = mAudioRecord
            r14.startRecording()
            goto L_0x0191
        L_0x018c:
            r29 = r1
            r26 = r14
            r1 = 1
        L_0x0191:
            android.media.AudioRecord r14 = mAudioRecord
            int r14 = r14.getSampleRate()
            r26[r24] = r14
            android.media.AudioRecord r14 = mAudioRecord
            int r14 = r14.getAudioFormat()
            r26[r1] = r14
            android.media.AudioRecord r14 = mAudioRecord
            int r14 = r14.getChannelCount()
            r15 = 2
            r26[r15] = r14
            r26[r23] = r2
            goto L_0x01fd
        L_0x01ad:
            r29 = r1
            r26 = r14
            r1 = 1
            android.media.AudioTrack r14 = mAudioTrack
            if (r14 != 0) goto L_0x01e2
            android.media.AudioTrack r27 = new android.media.AudioTrack
            r15 = 3
            int r19 = r2 * r21
            r20 = 1
            r14 = r27
            r16 = r0
            r17 = r3
            r18 = r13
            r14.<init>(r15, r16, r17, r18, r19, r20)
            mAudioTrack = r27
            int r14 = r27.getState()
            if (r14 == r1) goto L_0x01dd
            java.lang.String r1 = "Failed during initialization of Audio Track"
            android.util.Log.e(r11, r1)
            android.media.AudioTrack r1 = mAudioTrack
            r1.release()
            mAudioTrack = r25
            return r25
        L_0x01dd:
            android.media.AudioTrack r14 = mAudioTrack
            r14.play()
        L_0x01e2:
            android.media.AudioTrack r14 = mAudioTrack
            int r14 = r14.getSampleRate()
            r26[r24] = r14
            android.media.AudioTrack r14 = mAudioTrack
            int r14 = r14.getAudioFormat()
            r26[r1] = r14
            android.media.AudioTrack r14 = mAudioTrack
            int r14 = r14.getChannelCount()
            r15 = 2
            r26[r15] = r14
            r26[r23] = r2
        L_0x01fd:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            java.lang.StringBuilder r4 = r14.append(r4)
            if (r28 == 0) goto L_0x0209
            goto L_0x020a
        L_0x0209:
            r5 = r6
        L_0x020a:
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.String r5 = ", got "
            java.lang.StringBuilder r4 = r4.append(r5)
            r5 = r26[r23]
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r7)
            r5 = 2
            r5 = r26[r5]
            java.lang.StringBuilder r4 = r4.append(r5)
            java.lang.StringBuilder r4 = r4.append(r8)
            r1 = r26[r1]
            java.lang.String r1 = getAudioFormatString(r1)
            java.lang.StringBuilder r1 = r4.append(r1)
            java.lang.StringBuilder r1 = r1.append(r9)
            r4 = r26[r24]
            java.lang.StringBuilder r1 = r1.append(r4)
            java.lang.StringBuilder r1 = r1.append(r10)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r11, r1)
            return r26
        */
        throw new UnsupportedOperationException("Method not decompiled: org.libsdl.app.SDLAudioManager.open(boolean, int, int, int, int):int[]");
    }

    public static int[] audioOpen(int sampleRate, int audioFormat, int desiredChannels, int desiredFrames) {
        return open(false, sampleRate, audioFormat, desiredChannels, desiredFrames);
    }

    public static void audioWriteFloatBuffer(float[] buffer) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < buffer.length) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i, 0);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(float)");
                return;
            }
        }
    }

    public static void audioWriteShortBuffer(short[] buffer) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < buffer.length) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(short)");
                return;
            }
        }
    }

    public static void audioWriteByteBuffer(byte[] buffer) {
        if (mAudioTrack == null) {
            Log.e(TAG, "Attempted to make audio call with uninitialized audio!");
            return;
        }
        int i = 0;
        while (i < buffer.length) {
            int result = mAudioTrack.write(buffer, i, buffer.length - i);
            if (result > 0) {
                i += result;
            } else if (result == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                Log.w(TAG, "SDL audio: error return from write(byte)");
                return;
            }
        }
    }

    public static int[] captureOpen(int sampleRate, int audioFormat, int desiredChannels, int desiredFrames) {
        return open(true, sampleRate, audioFormat, desiredChannels, desiredFrames);
    }

    public static int captureReadFloatBuffer(float[] buffer, boolean blocking) {
        return mAudioRecord.read(buffer, 0, buffer.length, blocking ^ true ? 1 : 0);
    }

    public static int captureReadShortBuffer(short[] buffer, boolean blocking) {
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(buffer, 0, buffer.length);
        }
        return mAudioRecord.read(buffer, 0, buffer.length, blocking ^ true ? 1 : 0);
    }

    public static int captureReadByteBuffer(byte[] buffer, boolean blocking) {
        if (Build.VERSION.SDK_INT < 23) {
            return mAudioRecord.read(buffer, 0, buffer.length);
        }
        return mAudioRecord.read(buffer, 0, buffer.length, blocking ^ true ? 1 : 0);
    }

    public static void audioClose() {
        AudioTrack audioTrack = mAudioTrack;
        if (audioTrack != null) {
            audioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public static void captureClose() {
        AudioRecord audioRecord = mAudioRecord;
        if (audioRecord != null) {
            audioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }
}
