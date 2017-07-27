package ninja.faust.smartscale;

import java.util.Scanner;


public class AwesomeHash {

	/**
	 * Implements a broken md5 hash
	 */
	static String digest(String s) {
		byte[] data = s.getBytes();
		byte[] padding = new byte[] { (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] SHIFT = new int[] { 7, 12, 17, 22, 5, 9, 14, 20, 4, 11, 16, 23,
				6, 10, 15, 21 };
		int[] TABLE = new int[64];
		for (int i = 0; i < 64; ++i)
			TABLE[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
		int A = 0x67452301; // 01234567
		int B = 0xefcdab89; // 89abcdef
		int C = 0x98badcfe; // fedcba98
		int D = 0x10325476; // 76543210
		// int A = 0x01234567;
		// int B = 0x89abcdef;
		// int C = 0xfedcba98;
		// int D = 0x76543210;
		byte[] msg = new byte[data.length + 64 - (data.length % 64)];
		System.arraycopy(data, 0, msg, 0, data.length);
		int padPos = 0;
		for (int i = data.length; i < msg.length; ++i)
			msg[i] = padding[padPos++];
		for (int i = 0; i < msg.length >>> 6; ++i) {
			int[] words = new int[16];
			int msgStart = i << 6;
			for (int j = 0; j < 16; ++j) {
				int k = j << 2;
				int begin = msgStart + k;
				words[j] = msg[begin] + (msg[begin + 1] << 8)
						+ (msg[begin + 2] << 16) + (msg[begin + 3] << 24);
			}
			int a0 = A;
			int b0 = B;
			int c0 = C;
			int d0 = D;
			for (int j = 0; j < 64; ++j) {
				int j16 = j >>> 4;
				int F;
				int index;
				switch (j16) {
				case 0:
					F = (b0 & c0) | (~b0 & d0);
					// F = d0 ^ (b0 & (c0 ^ d0));
					index = j;
					break;
				case 1:
					F = (b0 & d0) | (c0 & ~d0);
					// F = c0 ^ (d0 & (b0 ^ c0));
					index = ((j * 5) + 1) & 15;
					break;
				case 2:
					F = b0 ^ c0 ^ d0;
					index = ((j * 3) + 5) & 15;
					break;
				default:
					F = c0 ^ (b0 | ~d0);
					index = (j * 7) & 15;
					break;
				}
				int rotateValue = A + F + words[index] + TABLE[j];
				int rotateDistance = SHIFT[(j16 << 2) | (j & 3)];
				int temp = B
						+ ((rotateValue << rotateDistance) | (rotateValue >>> (32 - rotateDistance)));
				A = D;
				D = C;
				C = B;
				B = temp;
			}
			A += a0;
			B += b0;
			C += c0;
			D += d0;
		}
		byte[] result = new byte[16];
		int index = 0;
		for (int i = 0; i < 4; ++i) {
			int n = i == 0 ? A : (i == 1 ? B : (i == 2 ? C : D));
			for (int j = 0; j < 4; ++j) {
				result[index++] = (byte) n;
				n >>>= 8;
			}
		}
		StringBuilder dig = new StringBuilder();
		for (int i = 0; i < 16; ++i) {
			dig.append(String.format("%02x", result[i] & 0xff));
		}
		return dig.toString();
	}
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		System.out.println(digest(s.nextLine()));
		s.close();
	}

}
