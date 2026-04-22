package uk.ac.brunel.types;

/**
 * The outcome of a decision process by federates/objects which culminates in either an acceptance (1) or a rejection (0).
 *
 * @author Hridyanshu Aatreya
 */
public enum OperationalVerdict {
    ACCEPTED ((short) 1),
    REJECTED((short) 0);

    private final short value;
    OperationalVerdict(short value) {
        this.value = value;
    }

    public static OperationalVerdict query(short value) {
        for (OperationalVerdict verdict : OperationalVerdict.values()) {
            if (verdict.value == value) {
                return verdict;
            }
        }

        return null;
    }

    public short getValue() {
        return value;
    }
}
