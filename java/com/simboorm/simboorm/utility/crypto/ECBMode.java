package com.simboorm.simboorm.utility.crypto;

import com.simboorm.simboorm.utility.crypto.BlockCipher;
import com.simboorm.simboorm.utility.crypto.BlockCipher.Mode;
import com.simboorm.simboorm.utility.crypto.BlockCipherModeBlock;

public class ECBMode extends BlockCipherModeBlock {

	public ECBMode(BlockCipher cipher) {
		super(cipher);
	}

	@Override
	public String getAlgorithmName() {
		return engine.getAlgorithmName() + "/ECB";
	}

	@Override
	public void init(Mode mode, byte[] mk) {
		this.mode = mode;
		engine.init(mode, mk);
	}

	@Override
	protected int processBlock(byte[] in, int inOff, byte[] out, int outOff, int outlen) {
		if (outlen != blocksize) {
			throw new IllegalArgumentException("outlen should be " + blocksize + " in " + getAlgorithmName());
		}

		if ((inOff + blocksize) > in.length) {
			throw new IllegalStateException("input data too short");
		}

		return engine.processBlock(in, inOff, out, outOff);
	}

}
