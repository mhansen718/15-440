import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;


public class MRRFamilyConfig extends ConfigurationMRR<FamilyRecord, HashSet<MemberRecord>, HashSet<MemberRecord>> {

	private static final long serialVersionUID = 5198221844897768850L;

	@Override
	public FamilyRecord readRecord(byte[] record) {
		FamilyRecord structRecord = new FamilyRecord();
		
		/* Add the individual to their family record */
		MemberRecord my = new MemberRecord();
		my.self = (new String(Arrays.copyOfRange(record, 0, 26)));
		my.birthyear = Integer.parseInt((new String(Arrays.copyOfRange(record, 26, 30))));
		structRecord.self = my;
		
		/* Add their ancestors */
		structRecord.family = new HashSet<MemberRecord>();
		for (int i = 0; i < 30; i++) {
			/* Run through the list immediate family members */
			int index = i * 30;
			if (Byte.toString(record[index]).equals(" ")) {
				break;
			} else {
				MemberRecord member = new MemberRecord();
				member.self = (new String(Arrays.copyOfRange(record, index, (index + 26))));
				member.birthyear = Integer.parseInt((new String(Arrays.copyOfRange(record, (index + 26), (index + 30)))));
				structRecord.family.add(member);
			}
		}
		return structRecord;
	}

	@Override
	public ArrayList<Pair<HashSet<MemberRecord>, HashSet<MemberRecord>>> map(
			FamilyRecord mapin) {
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

	@Override
	public byte[] writeRecord(
			Pair<HashSet<MemberRecord>, HashSet<MemberRecord>> record) {
		// TODO Auto-generated method stub
		return null;
	}

}