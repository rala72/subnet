// ralaVersion 1.1.0
// Methoden aus Subnetztabelle 1.2.6 [Ursprung: 1.1.8] entnommen
package rala;

import java.util.Set;
import java.util.TreeSet;

// mini bug: toString: " \t" between -> tab removed
/**
 * you need a IP-Address and a Subnetmask to get a Subnet<br>
 * <br>
 * this tool is in use from rala for his program: "Subnetztabelle"<br>
 * 
 * <i>you should catch IllegalArgumentExceptions</i>
 * 
 * @version 1.1.0
 * @author rala<br>
 *         {@link ralaweb@gmx.at}<br>
 *         {@link www.ralaweb.bplaced.net}
 */
public class Subnet implements Comparable<Subnet> {
	private static final int FehlerSpezialZahl = 0xDEADBEEF;
	private static final char[] Zeichen_letterWithSpezialLetterAndSymbolsWithoutSlash = { ' ',
			'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'ä', 'ö', 'ü', 'ß',
			'!', '?', ',', ':', ';', '"', '´', '`',
			'€', '$', '&', '%', '#', '§', '°',
			'\\', '(', ')', '{', '}', '[', ']',
			'=', '+', '-', '*', '~',
			'\'', '_', '<', '>', '|', '²', '³', '^' };
	
	// private static final String[] fehler_a_string = new String[0];
	private static final char[] Zeichen_Ziffer_noBi = { '2', '3', '4', '5', '6', '7', '8', '9' };
	private static final int[] snm_erlaubt_int = { 0, 128, 192, 224, 240, 248, 252, 254, 255, 256 };
	// 255: keine nutzbaren Host, 256: nur 1 Host
	
	public static final String Exception_message = "Subnet Error - ";
	/**
	 * ends with "true" = ip<br>
	 * ends with "false" = subnetmask
	 */
	public static final String IllegalArgument_EntryMissing = Exception_message + "Entry missing - maybe the entry is \"\" or \" \"";
	/**
	 * ends with "true" = ip<br>
	 * ends with "false" = subnetmask
	 */
	public static final String IllegalArgument_EntryNotSuppored = Exception_message + "Entry not supported / probably contains wrong characters: check it again!";
	/**
	 * ends with "true" = ip<br>
	 * ends with "false" = subnetmask
	 */
	public static final String IllegalArgument_EntrySizeToSmall = Exception_message + "Size of the entry is to small";
	/**
	 * ends with "true" = ip<br>
	 * ends with "false" = subnetmask
	 */
	public static final String IllegalArgument_EntrySizeToLarge = Exception_message + "Size of the entry is to large";
	
	// public static final String IllegalArgument_EntryContainsErrors = Exception_message + "Entry contais errors";// bad Exception
	
	public static final String IllegalArgument_SubnetmaskFirstQuadIsInteresting = "First Quad is not allowed to be the interesting one";
	public static final String IllegalArgument_SubnetmaskContainsWrongNumber = "Subnetmask contains wrong number";
	public static final String IllegalArgument_Subnetmask255to0 = "Subnetmask: unequal 255 -> next has to be 0";// RalaCharSymbols.arrow_east
	
	// public static final String Arithmetic_PrefixCalculation = Exception_message + "Prefix Error during calculation to normal Subnetmask declaration like 255.255.255.255";
	// public static final String Arithmetic_BinaryCalculation = Exception_message + "Binary Error during calculation to normal Subnetmask declaration like 255.255.255.255";
	public static final String IllegalArgument_FirstQuadIsNotTheSame = Exception_message + "Summarize Exception: please make sure that both have the same Network (1. Quad)";
	
	// das interesante Quad darf nicht das 1. sein
	// SNM enth\u00e4lt eine falsche Zahl
	// ungleich 255 "+RalaCharSymbols.arrow_east+" n\u00e4chste 0
	// SNM : sp\u00e4tere Zahlen d\u00fcrfen nicht gr\u00f6\u00dfer sein
	
	private String IP = "";
	private String SNM = "";
	// private boolean supernetting=false;
	private String[] IP_a = new String[4];
	private String[] SNM_a = new String[4];
	private String[] WILD_a = new String[4];
	private int IQ = -1;
	private int MZ = -1;
	private int MZ_min = -1;
	private int MZ_max = -1;
	
	private int zero_count = 0;
	
	private String[] subnetID_a = new String[4];
	private String[] firstAvailableIP_a = new String[4];
	private String[] lastAvailableIP_a = new String[4];
	private String[] broadCastIP_a = new String[4];
	private String[] classID_a = new String[4];
	private String[] classSNM_a = new String[4];
	
	private char classChar = ' ';
	
	private int Netbits;
	private String Netbits_s = "";
	private int Subnetbits;
	private String Subnetbits_s = "";
	private int Hostbits;
	private String Hostbits_s = "";
	private int CountOfSubnets;
	private String CountOfSubnets_s = "";
	private int CountOfHosts;
	private String CountOfHosts_s = "";
	
	/**
	 * generate a Subnet
	 * 
	 * @param IP IP-Adress
	 * @param SNM Subnetmase
	 */
	public Subnet(String IP, String SNM) {
		if (!setIP(IP, false)) {
			return;
		}
		if (!setSNM(SNM)) {
			return;
		}
		// System.out.println("Subnet: \t"+this.IP+" "+this.SNM);
	}
	
	/**
	 * generate a Subnet with Arrays
	 * 
	 * @param IP IP-Adress [4]
	 * @param SNM Subnetmase [4]
	 */
	public Subnet(String[] IP, String[] SNM) {
		if (!setIP(IP, false)) {
			return;
		}
		if (!setSNM(SNM)) {
			return;
		}
		// System.out.println("Subnet: \t"+this.IP+" "+this.SNM);
	}
	
	/**
	 * set IP to <i>iP</i><br>
	 * and recalculate
	 * 
	 * @param iP IP-Adress
	 * @return if entry is ok
	 */
	public boolean setIP(String iP) {
		return setIP(iP, true);
	}
	
	/**
	 * set IP to <i>iP</i>
	 * 
	 * @param iP IP-Adress
	 * @param reCalculate recalculate now or later..?
	 * @return if entry is ok
	 */
	public boolean setIP(String iP, boolean reCalculate) {
		if (!entryExists(iP)) {
			throw new IllegalArgumentException(IllegalArgument_EntryMissing + " " + true);
		}
		iP = addEnd0(iP);
		
		IP = iP;
		IP_a = iP.split("\\.");
		// System.out.println(RalaArrayToStringDetail.toString(IP_a));
		if (!isEntryOk(IP_a, true)) {
			return false;
		}
		if (reCalculate) {
			setSNM(SNM);
		}
		return true;
	}
	
	/**
	 * set IP to <i>iP</i>
	 * 
	 * @param iP IP-Adress
	 * @param reCalculate recalculate now or later..?
	 * @throws EntrySizeExceededException , EntryWrongCharacterException
	 * @return if entry is ok
	 */
	public boolean setIP(String[] iP, boolean reCalculate) {
		String iP_s = arrayToString(iP);
		return setIP(iP_s, reCalculate);
	}
	
	/**
	 * set IP-Adress
	 * 
	 * @param iP_a IP-Adress Array [4]
	 * @return if entry is ok
	 */
	public boolean setIP(String[] iP_a) {
		return setIP(arrayToString(iP_a));
	}
	
	/**
	 * set subnetmask & recalculate table NOW<br>
	 * 
	 * @param sNM subnetmask
	 * @return if entry is ok
	 */
	public boolean setSNM(String sNM) {
		if (!entryExists(sNM)) {
			// System.out.println("Subnet: Entry doesn't exist! [SNM]");
			throw new IllegalArgumentException(IllegalArgument_EntryMissing + " " + false);
		}
		sNM = addEnd0(sNM);
		
		SNM = sNM;
		SNM_a = sNM.split("\\.");
		// System.out.println(RalaArrayToStringDetail.toString(SNM_a));
		if (!isEntryOk(SNM_a, false)) {
			return false;
		}
		// System.out.println(SNM);
		calc();
		return true;
	}
	
	/**
	 * set subnetmask & recalculate table NOW<br>
	 * 
	 * @param sNM_a subnetmask Array [4]
	 * @throws EntrySizeExceededException , EntryWrongCharacterException
	 * @return if entry is ok
	 */
	public boolean setSNM(String[] sNM_a) {
		return setSNM(arrayToString(sNM_a));
	}
	
	/**
	 * @return IP-Address
	 */
	public String getIP() {
		return IP;
	}
	
	/**
	 * @return IP-Address as array
	 */
	public String[] getIP_array() {
		return IP_a;
	}
	
	/**
	 * @return subnetmask
	 * @see Subnet#getSubnetmask()
	 */
	public String getSNM() {
		return SNM;
	}
	
	/**
	 * @return subnetmask
	 * @see Subnet#getSubnetmask()
	 */
	public String getSubnetmask() {
		return SNM;
	}
	
	/**
	 * @return subnetmask as array
	 * @see Subnet#getSubnetmask_array()
	 */
	public String[] getSNM_array() {
		return SNM_a;
	}
	
	/**
	 * @return subnetmask as array
	 * @see Subnet#getSubnetmask_array()
	 */
	public String[] getSubnetmask_array() {
		return SNM_a;
	}
	
	/**
	 * @return Wildmask
	 */
	public String getWildmask() {
		return arrayToString(WILD_a);
	}
	
	/**
	 * @return Wildmask as array
	 */
	public String[] getWildmask_array() {
		return WILD_a;
	}
	
	/**
	 * 
	 * @return IQ from Array (0-3 NOT 1-4!!)
	 */
	public int getIQ() {
		return IQ;
	}
	
	/**
	 * @return magic number
	 */
	public int getMagicNumber() {
		return MZ;
	}
	
	/**
	 * <b><i>use it only if you know what you do!</i></b>
	 * 
	 * @return magic number - minimum
	 */
	public int getMagicNumber_min() {
		return MZ_min;
	}
	
	/**
	 * <b><i>use it only if you know what you do!</i></b>
	 * 
	 * @return magic number - maximum
	 */
	public int getMagicNumber_max() {
		return MZ_max;
	}
	
	/**
	 * @return Subnet ID
	 */
	public String getSubnetID() {
		return arrayToString(getSubnetID_array());
	}
	
	/**
	 * @return Subnet ID as array
	 */
	public String[] getSubnetID_array() {
		return subnetID_a;
	}
	
	/**
	 * @return First available IP address
	 */
	public String getFirstAvailableIP() {
		return arrayToString(getFirstAvailableIP_array());
	}
	
	/**
	 * @return First available IP address as array
	 */
	public String[] getFirstAvailableIP_array() {
		return firstAvailableIP_a;
	}
	
	/**
	 * @return Last available IP address
	 */
	public String getLastAvailableIP() {
		return arrayToString(getLastAvailableIP_array());
	}
	
	/**
	 * @return Last available IP address as array
	 */
	public String[] getLastAvailableIP_array() {
		return lastAvailableIP_a;
	}
	
	/**
	 * @return Broadcast IP address
	 */
	public String getBroadCastIP() {
		return arrayToString(getBroadCastIP_array());
	}
	
	/**
	 * @return Broadcast IP address as array
	 */
	public String[] getBroadCastIP_array() {
		return broadCastIP_a;
	}
	
	/**
	 * @return Class ID
	 */
	public String getClassID() {
		return arrayToString(getClassID_array());
	}
	
	/**
	 * @return Class ID as array
	 */
	public String[] getClassID_array() {
		return classID_a;
	}
	
	/**
	 * @return Class SNM
	 */
	public String getClassSNM() {
		return arrayToString(getClassSNM_array());
	}
	
	/**
	 * @return Class SNM as array
	 */
	public String[] getClassSNM_array() {
		return classSNM_a;
	}
	
	/**
	 * @return Character of the class
	 */
	public char getClassChar() {
		return classChar;
	}
	
	/**
	 * @return count of netbits
	 */
	public int getNetbits() {
		return Netbits;
	}
	
	/**
	 * @return count of netbits with calculated number if negative
	 */
	public String getNetbits_calc() {
		return Netbits_s;
	}
	
	/**
	 * @return count of subnetbits
	 */
	public int getSubnetbits() {
		return Subnetbits;
	}
	
	/**
	 * @return count of subnetbits with calculated number if negative
	 */
	public String getSubnetbits_calc() {
		return Subnetbits_s;
	}
	
	/**
	 * @return count of hostbits
	 */
	public int getHostbits() {
		return Hostbits;
	}
	
	/**
	 * @return count of hostbits with calculated number if negative
	 */
	public String getHostbits_calc() {
		return Hostbits_s;
	}
	
	/**
	 * @return count of subnets
	 */
	public int getCountOfSubnets() {
		return CountOfSubnets;
	}
	
	/**
	 * @return count of subnets with calculation
	 */
	public String getCountOfSubnets_calc() {
		return CountOfSubnets_s;
	}
	
	/**
	 * @return count of hosts in a subnet
	 */
	public int getCountOfHosts() {
		return CountOfHosts;
	}
	
	/**
	 * @return count of hosts in a subnet
	 */
	public String getCountOfHosts_calc() {
		return CountOfHosts_s;
	}
	
	/**
	 * check current network if it is super-netting
	 * 
	 * @return if this subnet is super-netting
	 */
	public boolean isSupernetting() {
		zero_count = 0;
		for (int i = 3; i >= 0; i--) {
			if (Integer.parseInt(SNM_a[i]) == 0) {
				zero_count += 8;
			} else if (Integer.parseInt(SNM_a[i]) != 255) {
				String snmIQ = Integer.toBinaryString(Integer.parseInt(SNM_a[i])) + "";
				for (int j = (snmIQ.length() - 1); j >= 0; j--) {
					if (snmIQ.charAt(j) == '0') {
						zero_count += 1;
					}
				}
			}
		}
		if (Integer.parseInt((String) IP_a[0]) > 223) {// Klasse D & E; ab 224 kein supernet
			return false;
		} else if (Integer.parseInt((String) IP_a[0]) > 191) {// SUPERNETTING : NUR wenn 192-223 (Klasse C)
			if ((8 - zero_count) < 0) {
				return true;
			}
		} else if (Integer.parseInt((String) IP_a[0]) > 127) {// NUR wenn 128-191 (Klasse B)
			if ((16 - zero_count) < 0) {
				return true;
			}
		} else {// NUR wenn 0-127 (Klasse A)
			if ((24 - zero_count) < 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * summarize current network with s
	 * 
	 * @param s Subnet to add
	 * @return the summarized network
	 */
	public Subnet summarize(Subnet s) {
		String erg_IP = "";
		String erg_SNM = "";
		if (this.getIQ() == -1) {
			// System.out.println("Summ.: Praefix error! [this]");
			return null;
		} else if (s.getIQ() == -1) {
			// System.out.println("Summ.: Praefix error! [s2]");
			return null;
		}
		if (!this.getIP_array()[0].equals(s.getIP_array()[0])) {
			// System.out.println("Summ.: "+this.getIP_array()[0]+"!="+s.getIP_array()[0]);
			throw new IllegalArgumentException(IllegalArgument_FirstQuadIsNotTheSame);
		}
		
		String sa_IP1[] = new String[4];
		String sa_IP2[] = new String[4];
		String s_IP1 = "";
		String s_IP2 = "";
		for (int i = 0; i < 4; i++) {
			sa_IP1[i] = Integer.toBinaryString(Integer.parseInt(this.getIP_array()[i]));
			sa_IP2[i] = Integer.toBinaryString(Integer.parseInt(s.getIP_array()[i]));
			for (int ii = sa_IP1[i].length(); ii < 8; ii++) {
				sa_IP1[i] = "0" + sa_IP1[i];
			}
			for (int ii = sa_IP2[i].length(); ii < 8; ii++) {
				sa_IP2[i] = "0" + sa_IP2[i];
			}
			if (i < 3) {
				// kann hier keinen Tab einsetzen, da Länge überprüft wird
				// ev. bei einem anderen Update
				s_IP1 += sa_IP1[i] + ".";
				s_IP2 += sa_IP2[i] + ".";
			} else {
				s_IP1 += sa_IP1[i];
				s_IP2 += sa_IP2[i];
			}
		}
		
		if (s_IP1.length() != 35) {// shouldn't be use
			// System.out.println("Summ.: IP1.length: "+(s_IP1.length()-3));
			return null;
		}
		if (s_IP2.length() != 35) {// shouldn't be use
			// System.out.println("Summ.: IP2.length: "+(s_IP2.length()-3));
			return null;
		}
		
		// System.out.println("Summ.: IP1:"+s_IP1);
		// System.out.println("Summ.: IP2:"+s_IP2);
		
		// 192.168.12.3 /23
		for (int i = 35; i > 0; i--) {
			if (s_IP1.substring(0, i).equals(s_IP2.substring(0, i))) {
				String zwIPsub = s_IP1.substring(0, i);
				zwIPsub = zwIPsub.replace(".", "");
				int len = zwIPsub.length();
				// System.out.println(zwIPsub+" ("+zwIPsub.length()+")");
				for (int ii = zwIPsub.length(); ii < 33; ii++) {
					zwIPsub += "0";
				}
				String zwIPsubF = "";
				for (int ii = 0; ii < 32; ii++) {
					zwIPsubF += zwIPsub.charAt(ii);
					if ((ii + 1) % 8 == 0) {
						zwIPsubF += ".";
					}
					if (ii == 31) {
						zwIPsub = zwIPsubF;
					}
				}
				// System.out.println(zwIPsub);
				String[] zwIPsubA = zwIPsub.split("\\.");
				String[] ipS = new String[4];
				for (int ii = 0; ii < 4; ii++) {
					ipS[ii] = Subnet.convertBinaryToDecimal(Long.parseLong(zwIPsubA[ii])) + "";
				}
				
				if (len == 32) {
					len = 8;
				}
				String zwSNM = "";
				for (int ii = 0; ii < 32; ii++) {
					if (ii < len) {
						zwSNM += "1";
					} else {
						zwSNM += "0";
					}
					if ((ii + 1) % 8 == 0 && ii < 31) {
						zwSNM += ".";
					}
				}
				String[] zwSNMa = zwSNM.split("\\.");
				String[] snmS = new String[4];
				for (int ii = 0; ii < 4; ii++) {
					snmS[ii] = Subnet.convertBinaryToDecimal(Long.parseLong(zwSNMa[ii])) + "";
				}
				// System.out.println("Summ.: SNM:"+zwSNM);
				
				for (int j = 0; j < 4; j++) {
					if (j < 3) {
						erg_IP += ipS[j] + ".";
						erg_SNM += snmS[j] + ".";
					} else {
						erg_IP += ipS[j];
						erg_SNM += snmS[j];
					}
				}
				
				/*
				 * for(int j=0; j<4; j++){
				 * 
				 * }
				 */
				break;
			}
		}// Ausgabe bei .18.128/25 - .20.0/24
		Subnet erg = new Subnet(erg_IP, erg_SNM);
		return erg;
	}
	
	/**
	 * summarize current network with subnet 2
	 * 
	 * @param s1 current network
	 * @param s2 Subnet to add
	 * @return the summarized network
	 */
	public static Subnet summarize(Subnet s1, Subnet s2) {
		String erg_IP = "";
		String erg_SNM = "";
		if (s1.getIQ() == -1) {
			// System.out.println("Summ.: Praefix error! [this]");
			return null;
		} else if (s2.getIQ() == -1) {
			// System.out.println("Summ.: Praefix error! [s2]");
			return null;
		}
		if (!s1.getIP_array()[0].equals(s2.getIP_array()[0])) {
			// System.out.println("Summ.: "+this.getIP_array()[0]+"!="+s.getIP_array()[0]);
			throw new IllegalArgumentException(IllegalArgument_FirstQuadIsNotTheSame);
		}
		
		String sa_IP1[] = new String[4];
		String sa_IP2[] = new String[4];
		String s_IP1 = "";
		String s_IP2 = "";
		for (int i = 0; i < 4; i++) {
			sa_IP1[i] = Integer.toBinaryString(Integer.parseInt(s1.getIP_array()[i]));
			sa_IP2[i] = Integer.toBinaryString(Integer.parseInt(s2.getIP_array()[i]));
			for (int ii = sa_IP1[i].length(); ii < 8; ii++) {
				sa_IP1[i] = "0" + sa_IP1[i];
			}
			for (int ii = sa_IP2[i].length(); ii < 8; ii++) {
				sa_IP2[i] = "0" + sa_IP2[i];
			}
			if (i < 3) {
				// kann hier keinen Tab einsetzen, da Länge überprüft wird
				// ev. bei einem anderen Update
				s_IP1 += sa_IP1[i] + ".";
				s_IP2 += sa_IP2[i] + ".";
			} else {
				s_IP1 += sa_IP1[i];
				s_IP2 += sa_IP2[i];
			}
		}
		
		if (s_IP1.length() != 35) {
			// shouldn't be use System.out.println("Summ.: IP1.length: "+(s_IP1.length()-3));
			return null;
		}
		if (s_IP2.length() != 35) {
			// shouldn't be use System.out.println("Summ.: IP2.length: "+(s_IP2.length()-3));
			return null;
		}
		
		// System.out.println("Summ.: IP1:"+s_IP1);
		// System.out.println("Summ.: IP2:"+s_IP2);
		
		// 192.168.12.3 /23
		for (int i = 35; i > 0; i--) {
			if (s_IP1.substring(0, i).equals(s_IP2.substring(0, i))) {
				String zwIPsub = s_IP1.substring(0, i);
				zwIPsub = zwIPsub.replace(".", "");
				int len = zwIPsub.length();
				// System.out.println(zwIPsub+" ("+zwIPsub.length()+")");
				for (int ii = zwIPsub.length(); ii < 33; ii++) {
					zwIPsub += "0";
				}
				String zwIPsubF = "";
				for (int ii = 0; ii < 32; ii++) {
					zwIPsubF += zwIPsub.charAt(ii);
					if ((ii + 1) % 8 == 0) {
						zwIPsubF += ".";
					}
					if (ii == 31) {
						zwIPsub = zwIPsubF;
					}
				}
				// System.out.println(zwIPsub);
				String[] zwIPsubA = zwIPsub.split("\\.");
				String[] ipS = new String[4];
				for (int ii = 0; ii < 4; ii++) {
					ipS[ii] = Subnet.convertBinaryToDecimal(Long.parseLong(zwIPsubA[ii])) + "";
				}
				
				if (len == 32) {
					len = 8;
				}
				String zwSNM = "";
				for (int ii = 0; ii < 32; ii++) {
					if (ii < len) {
						zwSNM += "1";
					} else {
						zwSNM += "0";
					}
					if ((ii + 1) % 8 == 0 && ii < 31) {
						zwSNM += ".";
					}
				}
				String[] zwSNMa = zwSNM.split("\\.");
				String[] snmS = new String[4];
				for (int ii = 0; ii < 4; ii++) {
					snmS[ii] = Subnet.convertBinaryToDecimal(Long.parseLong(zwSNMa[ii])) + "";
				}
				// System.out.println("Summ.: SNM:"+zwSNM);
				
				for (int j = 0; j < 4; j++) {
					if (j < 3) {
						erg_IP += ipS[j] + ".";
						erg_SNM += snmS[j] + ".";
					} else {
						erg_IP += ipS[j];
						erg_SNM += snmS[j];
					}
				}
				
				/*
				 * for(int j=0; j<4; j++){
				 * 
				 * }
				 */
				break;
			}
		}// Ausgabe bei .18.128/25 - .20.0/24
		Subnet erg = new Subnet(erg_IP, erg_SNM);
		return erg;
	}
	
	// TO DO calc
	private boolean calc() {
		// WILD SNM
		for (int i = 0; i < 4; i++) {
			WILD_a[i] = 255 - Integer.parseInt(SNM_a[i]) + "";
			// Magic Number
			setMagicNumber(i);
		}
		// SubnetID, firstAvailable, lastAvailable, Broadcast
		calc_addresses();
		
		// nets, subnets, host
		calc_bits();
		
		return true;
	}
	
	private boolean calc_addresses() {
		// 'reset' | default values
		for (int i = 0; i < 4; i++) {
			if (Integer.parseInt(SNM_a[i]) == 255 && i != IQ) {
				subnetID_a[i] = IP_a[i];
				firstAvailableIP_a[i] = IP_a[i];
				lastAvailableIP_a[i] = IP_a[i];
				broadCastIP_a[i] = IP_a[i];
			}
			if (i == 3) {
				if (Integer.parseInt(SNM_a[2]) != 255 && Integer.parseInt(SNM_a[3]) != 255 && i != IQ) {// ==0?
					subnetID_a[i] = 0 + "";
					firstAvailableIP_a[i] = 1 + "";
					lastAvailableIP_a[i] = 254 + "";
					broadCastIP_a[i] = 255 + "";
				}
			}
		}
		
		if (IQ == 1) {
			subnetID_a[IQ] = MZ_min + "";
			firstAvailableIP_a[IQ] = MZ_min + "";
			lastAvailableIP_a[IQ] = MZ_max + "";
			broadCastIP_a[IQ] = MZ_max + "";
			broadCastIP_a[IQ + 1] = MZ_max + "";
			
			// +1
			subnetID_a[IQ + 1] = 0 + "";
			firstAvailableIP_a[IQ + 1] = 0 + "";
			lastAvailableIP_a[IQ + 1] = 255 + "";
			broadCastIP_a[IQ + 1] = 255 + "";
		} else if (IQ == 2) {
			subnetID_a[IQ] = MZ_min + "";
			firstAvailableIP_a[IQ] = MZ_min + "";
			lastAvailableIP_a[IQ] = MZ_max + "";
			broadCastIP_a[IQ] = MZ_max + "";
		} else if (IQ == 3) {
			subnetID_a[IQ] = MZ_min + "";
			if ((MZ_min + 1) < MZ_max) {
				firstAvailableIP_a[IQ] = (MZ_min + 1) + "";
			} else {
				firstAvailableIP_a[IQ] = MZ_min + "";
			}
			
			if ((MZ_max - 1) > 0) {
				lastAvailableIP_a[IQ] = (MZ_max - 1) + "";
			} else {
				lastAvailableIP_a[IQ] = MZ_max + "";
			}
			
			broadCastIP_a[IQ] = MZ_max + "";
		}
		
		return true;
	}
	
	private boolean calc_bits() {
		for (int i = 3; i >= 0; i--) {
			if (Integer.parseInt(SNM_a[i]) == 0) {
				zero_count += 8;
			} else if (Integer.parseInt(SNM_a[i]) != 255) {
				String snmIQ = Integer.toBinaryString(Integer.parseInt(SNM_a[i])) + "";
				for (int j = (snmIQ.length() - 1); j >= 0; j--) {
					if (snmIQ.charAt(j) == '0') {
						zero_count += 1;
					}
				}
			}
		}
		
		// makeTabel_klassenNetzUndBitsUNDAnzahl
		// KlassenNetz
		for (int i = 1; i < 4; i++) {
			classID_a[i] = 0 + "";
			classSNM_a[i] = 0 + "";
		}
		
		// snm_erlaubt=0,128,192,224,240,248,252,254,255
		// Klassengrenzen:/8,/16,/24
		
		CountOfHosts = (int) (Math.pow(2, zero_count) - 2);
		CountOfHosts_s = "2^" + zero_count + "-2 = " + (int) (Math.pow(2, zero_count) - 2);
		
		if (Integer.parseInt(IP_a[0]) >= 0) {
			classID_a[0] = IP_a[0];
			classSNM_a[0] = 255 + "";
			if (Integer.parseInt(IP_a[0]) > 127) {
				classID_a[1] = IP_a[1];
				classSNM_a[1] = 255 + "";
				if (Integer.parseInt(IP_a[0]) > 191) {
					classID_a[2] = IP_a[2];
					classSNM_a[2] = 255 + "";
					if (Integer.parseInt(IP_a[0]) > 223) {// Klasse D & E; ab 224
						if (Integer.parseInt(IP_a[0]) > 223) {
							classChar = 'D';
						} else if (Integer.parseInt(IP_a[0]) > 239) {
							classChar = 'E';
						}
						
						Netbits_s = (32 - zero_count) + "";// ?
						Subnetbits_s = 0 + "";
						Hostbits_s = zero_count + "";
						
						CountOfSubnets = (int) (Math.pow(2, 0));
						CountOfSubnets_s = "2^" + 0 + " = " + (int) (Math.pow(2, 0));
					} else {// SUPERNETTING : NUR wenn 192-223 (Klasse C)
						classChar = 'C';
						if ((8 - zero_count) < 0) {
							// supernetting = true;
							
							Netbits_s = (32 - zero_count) + " (24)";
							Subnetbits_s = "0 (" + (8 - zero_count) + ")";
							Hostbits_s = zero_count + " ";
						} else {
							Netbits_s = 24 + "";
							Subnetbits_s = (8 - zero_count) + " ";
							Hostbits_s = zero_count + " ";
						}
						
						CountOfSubnets = (int) (Math.pow(2, (8 - zero_count)));
						CountOfSubnets_s = "2^" + (8 - zero_count) + " = " + (int) (Math.pow(2, (8 - zero_count)));
					}
				} else {// NUR wenn 128-191 (Klasse B)
					classChar = 'B';
					if ((16 - zero_count) < 0) {
						// supernetting=true;
						
						Netbits_s = (32 - zero_count) + " (16)";
						Subnetbits_s = "0 (" + (16 - zero_count) + ")";
						Hostbits_s = (zero_count) + " ";
					} else {
						Netbits_s = 16 + "";
						Subnetbits_s = 16 - zero_count + " ";
						Hostbits_s = (zero_count) + " ";
					}
					
					CountOfSubnets = (int) (Math.pow(2, (16 - zero_count)));
					CountOfSubnets_s = "2^" + (16 - zero_count) + " = " + (int) (Math.pow(2, (16 - zero_count)));
				}
			} else {// NUR wenn 0-127 (Klasse A)
				classChar = 'A';
				if ((24 - zero_count) < 0) {
					// supernetting=true;
					
					Netbits_s = (32 - zero_count) + " (8)";
					Subnetbits_s = "0 (" + (24 - zero_count) + ")";
					Hostbits_s = zero_count + " ";
				} else {
					Netbits_s = 8 + "";
					Subnetbits_s = 24 - zero_count + " ";
					Hostbits_s = zero_count + " ";
				}
				
				CountOfSubnets = (int) (Math.pow(2, (24 - zero_count)));
				CountOfSubnets_s = "2^" + (24 - zero_count) + " = " + (int) (Math.pow(2, (24 - zero_count)));
			}
		}
		
		return true;
	}
	
	/**
	 * check if entry is valid
	 * 
	 * @param s IP or Subetmask
	 * @return if entry exists
	 */
	private boolean entryExists(String s) {
		String t = s.replace(".", "");
		t = t.replace("/", "");
		if (t.equals(" ") || t.equals("")) {
			return false;
		}
		return true;
	}
	
	/**
	 * add .0 if <b>s</b> isn't complete
	 * 
	 * @param s IP or Subnetmask
	 */
	private String addEnd0(String s) {
		int anzPoints = 0;
		while (s.indexOf("..") > -1) {
			s = s.replace("..", ".");
		}
		if (s.endsWith(".")) {
			s = s.substring(0, s.length() - 1);
		}
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '.') {
				anzPoints++;
			}
		}
		for (int i = anzPoints; i < 3; i++) {
			s += ".0";
		}
		return s;
	}
	
	/**
	 * tests if IP and Subnetmask is correct
	 * 
	 * @return if IP and Subnetmask is correct
	 */
	private boolean isEntryOk(String[] entry, boolean isIP) {
		boolean pr_b = false;
		// System.out.println(IP+" "+SNM);
		for (int i = 0; i < 4; i++) {
			if (entry[i].equalsIgnoreCase("") || entry[i].equalsIgnoreCase(" ")) {
				// System.out.println("Subnet: Entry doesn't exist! [IP] ss["+i+"]");
				// System.out.println("Subnet: Entry doesn't exist! [SNM] ["+i+"]");
				throw new IllegalArgumentException(IllegalArgument_EntryMissing + " " + isIP);
			}
			if (isIP) {
				if (Test(entry[i]) == FehlerSpezialZahl || entry[i].contains("/")) {
					// System.out.println("Subnet: Entry has wrong character! [IP:"+i+"]");
					throw new IllegalArgumentException(IllegalArgument_EntryNotSuppored + " " + isIP);
				}
				if (Integer.parseInt(entry[i]) > 255) {
					// System.out.println("Subnet: Size to big! [IP:"+i+"]");
					throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge + " " + isIP);
				}
			} else {
				if (Test(entry[i]) == FehlerSpezialZahl) {
					// System.out.println("Subnet: Entry has wrong character! [SNM:"+i+"]");
					throw new IllegalArgumentException(IllegalArgument_EntryNotSuppored + " " + isIP);
				} else if (i == 0 && entry[0].charAt(0) == '/') {
					pr_b = true;
				} else if (Integer.parseInt(entry[i]) > 100000000 || (!pr_b && Integer.parseInt(entry[i]) > 11111111)) {
					// System.out.println("Subnet: Size to big! [SNM:"+i+"]");
					throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge + " " + isIP);
				}
				
				// PräfixTest
				if (pr_b == true && i == 0) {
					SNM_a = prafixTest(SNM_a);
				}
				
				// binärTest
				binarySNM(SNM_a, i);
				
				// Subnetmask check
				isSubnetzOk(SNM_a, i);
				
				setMagicNumber(i);
				
				SNM = "";
				for (int k = 0; k < 4; k++) {
					if (k < 3) {
						SNM += SNM_a[k] + ".";
					} else {
						SNM += SNM_a[k];
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * convert Präfix to decimal subnetmask
	 * 
	 * @param sa Subnetmask String Array
	 * @return converted subnetmask array
	 */
	private String[] prafixTest(String[] sa) {
		int pr_laenge = 0;
		String s_snm_pr = "";
		
		String pr_ss = sa[0].replace("/", "");
		
		if (Test(pr_ss) == FehlerSpezialZahl) {
			throw new IllegalArgumentException(IllegalArgument_EntryNotSuppored + " " + false);
		}
		if (pr_ss.equalsIgnoreCase("") || pr_ss.equalsIgnoreCase(" ")) {
			throw new IllegalArgumentException(IllegalArgument_EntryMissing + " " + false);
		}
		pr_laenge = Integer.parseInt(pr_ss);
		// System.out.println("Subnet: pr:substring:"+pr_laenge);
		if (pr_laenge < 8) {
			throw new IllegalArgumentException(IllegalArgument_EntrySizeToSmall + " " + false);
		} else if (pr_laenge > 31) {
			throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge + " " + false);
		}
		for (int j = 0; j < pr_laenge; j++) {
			s_snm_pr += "1";
			if ((j + 1) % 8 == 0) {
				s_snm_pr += ".";
			}
		}
		for (int j = pr_laenge; j < 32; j++) {
			s_snm_pr += "0";
			if ((j + 1) % 8 == 0 && (j + 1) != 32) {
				s_snm_pr += ".";
			}
		}
		return sa = s_snm_pr.split("\\.");
	}
	
	/**
	 * convert binary Subnetmask to decimal
	 * 
	 * @param a_snm subnetmask String Array
	 * @param i actuall Array (max 3) index
	 * @param a_ipAdresse IP-Address String Array
	 * @return if binarySNM is correctly converted
	 */
	private void binarySNM(String[] a_snm, int i) {// makeTable_Meth
		if (a_snm[i].length() >= 4 && Test_B(Integer.parseInt(a_snm[i])) != FehlerSpezialZahl && Integer.parseInt(a_snm[i]) != 0) {
			if (i == 0) {
				// System.out.println(RalaArrayToStringDetail.toString(a_snm));
			}
			
			// System.out.println(a_snm[i]);
			if (8 > a_snm[i].length()) {
				String n = "0";
				int p = 8 - a_snm.length;
				for (int o = 0; o <= p; o++) {
					n += "0";
				}
				a_snm[i] += n;
				// System.out.println(a_snm[i]);
			}
			
			a_snm[i] = convertBinaryToDecimal(Long.parseLong(a_snm[i])) + "";
			
			if (Integer.parseInt(a_snm[i]) > 256) {// darf NIE true sein!
				// System.out.println("Subnet: snm:"+i+" FEHLERHAFT umgewandelt!\t["+a_snm[i]+"]");
				throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge + " " + false);
			} else {
				// System.out.println("Subnet: snm:"+i+" erfolgreich umgewandelt!\t["+a_snm[i]+"]");
			}
		} else {// Binär:Fehler_l_ oder bereits richtiges Format
			if (Test(a_snm[i]) == FehlerSpezialZahl) {
				// System.out.println("Subnet: snm:"+i+": !! Fehler_l_:Eingabe !!");
				throw new IllegalArgumentException(IllegalArgument_EntryNotSuppored);
			} else if (Integer.parseInt(a_snm[i]) > 256) {
				throw new IllegalArgumentException(IllegalArgument_EntrySizeToLarge);
			} else if (Test_B(Integer.parseInt(a_snm[i])) != FehlerSpezialZahl) {
				if (a_snm[i].indexOf("1") < 0) {
					// System.out.println(a_snm[i]);
					a_snm[i] = "0";// 8mal 0 ausbessern
				}
				// System.out.println("Subnet: snm:"+i+" ist bereits dezimal!\t["+a_snm[i]+"]");
			}
		}
	}
	
	/**
	 * test subnetmask
	 * 
	 * @param a_snm Subnetmask String Array
	 * @param i actual array (max 3) index
	 * @return if it's ok
	 */
	private void isSubnetzOk(String[] a_snm, int i) {
		boolean snm_erlaubt_b = false;
		for (int j = 0; j < snm_erlaubt_int.length; j++) {
			if (Integer.parseInt(a_snm[i]) == snm_erlaubt_int[j]) {
				snm_erlaubt_b = true;
			}
		}
		if (snm_erlaubt_b == false) {
			throw new IllegalArgumentException(IllegalArgument_SubnetmaskContainsWrongNumber);
		}
		
		if (i != 3) {
			if (!(a_snm[i].equalsIgnoreCase(255 + "") || a_snm[i + 1].equalsIgnoreCase(0 + "") || a_snm[i + 1].equalsIgnoreCase("00000000"))) {
				boolean only0 = true;
				for (int j = 0; j < a_snm[i + 1].length(); j++) {
					if (a_snm[i + 1].charAt(j) != '0') {
						only0 = false;
						break;
					}
				}
				
				if (!only0) {
					throw new IllegalArgumentException(IllegalArgument_Subnetmask255to0);
				}
			}
		}
	}
	
	/**
	 * set MagicNumber
	 * 
	 * @param i actual Array index (mind 1, max 3) [IQ]
	 * @return if it's set correctly
	 */
	private void setMagicNumber(int i) {
		if (Integer.parseInt(SNM_a[i]) != 255 && IQ == -1) {// Magische Zahl
			MZ = 256 - Integer.parseInt(SNM_a[i]);
			// if(mz==256)
			IQ = i;
			// System.out.println("Subnet: iq:"+i);
			if (IQ == 0) {
				throw new IllegalArgumentException(IllegalArgument_SubnetmaskFirstQuadIsInteresting);
			}
		}
		
		if (IQ != -1) {
			for (int j = 0; j < 130; j++) {
				if ((MZ * j) <= Integer.parseInt(IP_a[IQ])) {
					MZ_min = MZ * j;
					MZ_max = MZ_min + MZ - 1;
					if (MZ_max == -1) {
						MZ_max = MZ - 1;
					}
				} else {
					break;
				}
			}
		}
		// System.out.println("Subnet: mz:"+MZ+"\tmz:min:"+MZ_min+"\tmz:max:"+MZ_max);
	}
	
	/**
	 * convert IP or SNM Array to String
	 * 
	 * @param IP IP Array [4]
	 * @return IP String
	 */
	private String arrayToString(String[] array) {
		String s = "";
		for (int i = 0; i < 4; i++) {
			if (i < 3) {
				s += array[i] + ".";
			} else {
				s += array[i];
			}
		}
		return s;
	}
	
	/**
	 * returns all Subnets in this network<br>
	 * <i>just compare the network with the first available IP to check which subnet is the current</i>
	 * 
	 * @return Set with all Subnets
	 */
	// see getAllNetworksFromTo
	public Set<Subnet> getSubnets() {
		Set<Subnet> subnets = new TreeSet<>();
		for (int iq_count = 0; iq_count <= 255; iq_count += getMagicNumber()) {
			switch (getIQ()) {
				case 1:
					subnets.add(new Subnet(new String[] { getIP_array()[0] + "", iq_count + "", "0", "0" }, getSNM_array()));
					break;
				case 2:
					subnets.add(new Subnet(new String[] { getIP_array()[0] + "", getIP_array()[1] + "", iq_count + "", "0" }, getSNM_array()));
					break;
				case 3:
					subnets.add(new Subnet(new String[] { getIP_array()[0] + "", getIP_array()[1] + "", getIP_array()[2] + "", iq_count + "" }, getSNM_array()));
					break;
			}
		}
		return subnets;
	}
	
	/**
	 * returns Subnets from current network<br>
	 * <b>NOTICE</b> it can take a while to get all
	 * 
	 * @return Set with all Subnets
	 */
	public Set<Subnet> getAllSubnets() {
		return getAllNetworksFromTo(new Subnet(getFirstAvailableIP(), getSNM()), new Subnet(getLastAvailableIP(), getSNM()));
	}
	
	/**
	 * returns Subnets from IP to IP<br>
	 * Subnetmask is taken from first network
	 * 
	 * @param from network with start IP
	 * @param to network with stop IP
	 * @return Set with all Subnets between
	 */
	protected static Set<Subnet> getAllNetworksFromTo(Subnet from, Subnet to) {
		Set<Subnet> subnets = new TreeSet<>();
		for (int from0 = Integer.parseInt(from.getIP_array()[0]); from0 <= Integer.parseInt(to.getIP_array()[0]); from0++) {
			for (int from1 = Integer.parseInt(from.getIP_array()[1]); from1 <= Integer.parseInt(to.getIP_array()[1]); from1++) {
				for (int from2 = Integer.parseInt(from.getIP_array()[2]); from2 <= Integer.parseInt(to.getIP_array()[2]); from2++) {
					for (int from3 = Integer.parseInt(from.getIP_array()[3]); from3 <= Integer.parseInt(to.getIP_array()[3]); from3++) {
						subnets.add(new Subnet(new String[] { from0 + "", from1 + "", from2 + "", from3 + "" }, from.getSNM_array()));
					}
				}
			}
		}
		return subnets;
	}
	
	// Umwandler
	// Binär
	/**
	 * convert binary to decimal
	 * 
	 * @param b binary number
	 * @return decimal number
	 */
	public static long convertBinaryToDecimal(long b) {
		if (Test_B(b) == FehlerSpezialZahl) {
			return FehlerSpezialZahl;
		}
		
		String erg = "";
		String zw = b + "";
		
		/*
		 * alter Test for(int i=0; i<zw.length(); i++){ if(Integer.parseInt(zw.charAt (i)+"")>1||Integer.parseInt(zw.charAt(i)+"")<0){ ausg.set Text(Fehler_l_EingabeFormat); return Fehler_l_SpezialZahl; } }
		 */
		
		double m = 0;
		long e = 0;
		for (int i = zw.length() - 1; i >= 0; i--) {
			if (zw.charAt(i) == '1') {
				e += Math.pow(2.0, m);
			}
			m++;
		}
		erg = e + "";
		return Integer.parseInt(erg);
	}
	
	// Test-s
	private static long Test(String text) {// darf auf protected/public sein//allgemein -- geändert
		for (int i = 0; i < Zeichen_letterWithSpezialLetterAndSymbolsWithoutSlash.length; i++) {
			if ((text + "").indexOf(Zeichen_letterWithSpezialLetterAndSymbolsWithoutSlash[i] + "") >= 0) {
				return FehlerSpezialZahl;
			} else if ((text + "").indexOf((Zeichen_letterWithSpezialLetterAndSymbolsWithoutSlash[i] + "").toUpperCase()) >= 0) {
				return FehlerSpezialZahl;
			}
		}
		return 1;
	}
	
	private static long Test_B(long zahl) {
		long t = (long) (Test(zahl + ""));
		if (t == FehlerSpezialZahl) {
			return FehlerSpezialZahl;
		} else {
			for (int i = 0; i < Zeichen_Ziffer_noBi.length; i++) {
				if ((zahl + "").indexOf(Zeichen_Ziffer_noBi[i] + "") >= 0) {
					return FehlerSpezialZahl;
				}
			}
			return 1;
		}
	}
	
	/**
	 * @return IP and Subnetzmaske and other infos
	 */
	public String toString(boolean detailed) {
		if (!detailed) {
			return toString();
		}
		String supernetting = "";
		if (isSupernetting()) {
			supernetting = "\t\tsupernetting";
		}
		
		String s = "\nSubnet-INFO:\n";
		s += getIP() + "\t" + getSNM() + "\t(" + getWildmask() + ")\tQuad: " + getIQ() + supernetting + "\n";
		s += "mz:" + getMagicNumber() + "\t\tmz:min:" + getMagicNumber_min() + "\tmz:max:" + getMagicNumber_max() + "\n";
		s += "subnet ID: \t" + getFirstAvailableIP() + "\nBroadcast: \t" + getLastAvailableIP() + "\n";
		s += "first available IP: \t" + getFirstAvailableIP() + "\nlast available IP: \t" + getLastAvailableIP() + "\n";
		s += "class: \t" + getClassChar() + "\nclass ID: \t" + getClassID() + "\nclass SNM: \t" + getClassSNM() + "\n";
		s += "netbits: " + getNetbits_calc() + " \t\tsubetsbits: " + getSubnetbits_calc() + " \thostsbits: " + getHostbits_calc() + "\n";
		s += "count of subnets: " + getCountOfSubnets_calc() + " \tcount of hosts: " + getCountOfHosts_calc();
		return s;
	}
	
	/**
	 * @return IP and Subnetzmaske
	 */
	public String toString() {
		String s = IP + " " + SNM;
		return s;
	}
	
	@Override
	public int compareTo(Subnet s) {
		for (int i = 0; i < 4; i++) {
			int ip = Integer.parseInt(getIP_array()[i]) - Integer.parseInt(s.getIP_array()[i]);
			if (ip != 0) {
				return ip;
			}
		}
		for (int i = 0; i < 4; i++) {
			int snm = Integer.parseInt(getSNM_array()[i]) - Integer.parseInt(s.getSNM_array()[i]);
			if (snm != 0) {
				return snm;
			}
		}
		return 0;
	}
}