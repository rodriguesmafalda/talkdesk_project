package talkdesk.mafalda.calls.repos;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import talkdesk.mafalda.calls.model.Call;

import java.util.List;

@Repository
public interface CallRepository extends JpaRepository<Call, Long> {

    /**
     * @param status   call status
     * @param pageable page
     * @return page of calls by status
     */
    Page<Call> findCallsByStatus(String status, Pageable pageable);

    /**
     * @param type   call status
     * @param pageable page
     * @return page of calls by status
     */
    Page<Call> findCallsByType(String type, Pageable pageable);

    /**
     * @param status   call status
     * @param type     call type
     * @param pageable page
     * @return page of calls by status and type
     */
    Page<Call> findAllByStatusAndType(String status, String type, Pageable pageable);

    /**
     * @param type   call type
     * @param status call status
     * @return list of calls
     */
    List<Call> findCallsByTypeAndStatus(String type, String status);

    /**
     * @param status call status
     * @return list of calls
     */
    List<Call> findCallsByStatus(String status);

}

