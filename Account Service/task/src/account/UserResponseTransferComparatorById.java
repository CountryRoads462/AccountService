package account;

import java.util.Comparator;
import java.util.Map;

public class UserResponseTransferComparatorById implements Comparator<UserResponseTransfer> {

    @Override
    public int compare(UserResponseTransfer o1, UserResponseTransfer o2) {
        return Long.compare(o1.getId(), o2.getId());
    }
}
