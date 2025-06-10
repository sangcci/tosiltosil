package tosiltosil.backend.module.goal.domain;

import java.util.UUID;

public interface DailyTotalTimeRepository {

    DailyTotalTime findByMemberId(UUID memberId);

    void save(DailyTotalTime dailyTotalTime);
}
