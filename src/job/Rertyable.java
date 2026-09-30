package job;

import service.PolicyRepository;

public interface Retryable {

    int MAX_RETRIES = 3;

    boolean canRetry();

    void retry(PolicyRepository repository);
}
