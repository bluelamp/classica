package com.simboorm.simboorm.utility.crypto;

import com.simboorm.simboorm.utility.crypto.BlockCipher;
import com.simboorm.simboorm.utility.crypto.LeaEngine;
import com.simboorm.simboorm.utility.crypto.CMac;
import com.simboorm.simboorm.utility.crypto.CBCMode;
import com.simboorm.simboorm.utility.crypto.CCMMode;
import com.simboorm.simboorm.utility.crypto.CFBMode;
import com.simboorm.simboorm.utility.crypto.CTRMode;
import com.simboorm.simboorm.utility.crypto.ECBMode;
import com.simboorm.simboorm.utility.crypto.GCMMode;
import com.simboorm.simboorm.utility.crypto.OFBMode;

public class LEA {
	private LEA() {
		throw new AssertionError();
	}

	public static final BlockCipher getEngine() {
		return new LeaEngine();
	}

	public static final class ECB extends ECBMode {
		public ECB() {
			super(getEngine());
		}
	}

	public static final class CBC extends CBCMode {
		public CBC() {
			super(getEngine());
		}
	}

	public static final class CTR extends CTRMode {
		public CTR() {
			super(getEngine());
		}
	}

	public static final class CFB extends CFBMode {
		public CFB() {
			super(getEngine());
		}
	}

	public static final class OFB extends OFBMode {
		public OFB() {
			super(getEngine());
		}
	}

	public static final class CCM extends CCMMode {
		public CCM() {
			super(getEngine());
		}
	}

	public static final class GCM extends GCMMode {
		public GCM() {
			super(getEngine());
		}
	}

	public static final class CMAC extends CMac {
		public CMAC() {
			super(getEngine());
		}
	}

}
