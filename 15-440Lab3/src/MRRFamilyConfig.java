import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


public class MRRFamilyConfig extends ConfigurationMRR<FamilyRecords, String, HashSet<MemberRecord>> {

	private static final long serialVersionUID = 5198221844897768850L;

	@Override
	public FamilyRecords readRecord(byte[] record) throws Exception {
		FamilyRecords structRecord = new FamilyRecords();
		structRecord.parents = new HashSet<MemberRecord>();
		
		/* Add person */
		MemberRecord mem = new MemberRecord();
		String fullName = (new String(Arrays.copyOfRange(record, 0, 26))).trim();
		mem.firstName = fullName.split(" ")[0];
		mem.lastName = fullName.split(" ")[1];
		mem.birthyear = Integer.parseInt((new String(Arrays.copyOfRange(record, 26, 30))));
		structRecord.self = mem;
		
		/* Add father */
		mem = new MemberRecord();
		fullName = (new String(Arrays.copyOfRange(record, 30, 56))).trim();
		mem.firstName = fullName.split(" ")[0];
		mem.lastName = fullName.split(" ")[1];
		mem.birthyear = Integer.parseInt((new String(Arrays.copyOfRange(record, 56, 60))));
		structRecord.parents.add(mem);
		
		/* Add mother */
		mem = new MemberRecord();
		fullName = (new String(Arrays.copyOfRange(record, 60, 86))).trim();
		mem.firstName = fullName.split(" ")[0];
		mem.lastName = fullName.split(" ")[1];
		mem.birthyear = Integer.parseInt((new String(Arrays.copyOfRange(record, 86, 90))));
		structRecord.parents.add(mem);

		return structRecord;
	}

	@Override
	public ArrayList<Pair<String, HashSet<MemberRecord>>> map(FamilyRecords mapin) throws Exception {
		ArrayList<Pair<String, HashSet<MemberRecord>>> returnPairList = new ArrayList<Pair<String, HashSet<MemberRecord>>>();
		for (MemberRecord mem : mapin.parents) {
			HashSet<MemberRecord> family = new HashSet<MemberRecord>();
			family.add(mapin.self);
			family.addAll(mapin.parents);
			returnPairList.add(new Pair<String, HashSet<MemberRecord>>(mem.lastName, family));
		}
		return returnPairList;
	}

	@Override
	public HashSet<MemberRecord> reduce(HashSet<MemberRecord> val1,
			HashSet<MemberRecord> val2) throws Exception {
		HashSet<MemberRecord> mutual = new HashSet<MemberRecord>(val1);
		mutual.addAll(val2);
		return mutual;
	}



}