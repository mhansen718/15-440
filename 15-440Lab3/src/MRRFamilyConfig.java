import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


public class MRRFamilyConfig extends ConfigurationMRR<FamilyRecords, String, HashSet<MemberRecord>> {

	private static final long serialVersionUID = 5198221844897768850L;

	@Override
	public FamilyRecords readRecord(byte[] record) {
		FamilyRecords structRecord = new FamilyRecords();
		
		/* Add person */
		MemberRecord mem = new MemberRecord();
		String fullName = (new String(Arrays.copyOfRange(record, 0, 26))).trim();
		mem.firstName = fullName.split(" ")[0];
		mem.lastName = fullName.split(" ")[1];
		mem.birthyear = Integer.parseInt((new String(Arrays.copyOfRange(record, 26, 30))));
		structRecord.members.add(mem);
		
		/* Add father */
		fullName = (new String(Arrays.copyOfRange(record, 30, 56))).trim();
		mem.firstName = fullName.split(" ")[0];
		mem.lastName = fullName.split(" ")[1];
		mem.birthyear = Integer.parseInt((new String(Arrays.copyOfRange(record, 56, 60))));
		structRecord.members.add(mem);
		
		/* Add mother */
		fullName = (new String(Arrays.copyOfRange(record, 60, 86))).trim();
		mem.firstName = fullName.split(" ")[0];
		mem.lastName = fullName.split(" ")[1];
		mem.birthyear = Integer.parseInt((new String(Arrays.copyOfRange(record, 86, 90))));
		structRecord.members.add(mem);
		
		return structRecord;
	}

	@Override
	public ArrayList<Pair<HashSet<MemberRecord>, HashSet<MemberRecord>>> map(
			FamilyRecords mapin) {
		ArrayList<Pair<HashSet<MemberRecord>, HashSet<MemberRecord>>> returnPairList = new ArrayList<Pair<HashSet<MemberRecord>, HashSet<MemberRecord>>>();
		for (MemberRecord mem : mapin.family) {
			HashSet<MemberRecord> pairing = new HashSet<MemberRecord>();
			pairing.add(mapin.self);
			pairing.add(mem);
			HashSet<MemberRecord> currentMutual = new HashSet<MemberRecord>(mapin.family);
			Pair<HashSet<MemberRecord>, HashSet<MemberRecord>> p = new Pair<HashSet<MemberRecord>, HashSet<MemberRecord>>(pairing, currentMutual);
			returnPairList.add(p);
		}
		return null;
	}

	@Override
	public HashSet<MemberRecord> reduce(HashSet<MemberRecord> val1,
			HashSet<MemberRecord> val2) {
		HashSet<MemberRecord> mutual = new HashSet<MemberRecord>(val1);
		mutual.retainAll(val2);
		return mutual;
	}



}