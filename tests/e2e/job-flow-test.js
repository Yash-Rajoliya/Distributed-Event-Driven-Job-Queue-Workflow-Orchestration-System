import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    thresholds: {
        checks: ['rate==1.0'],
    },
};

export default function () {
    const url = 'http://localhost:8080/jobs/status';
    const maxAttempts = 10;
    const pollIntervalSeconds = 1;

    let jobCompleted = false;

    for (let attempt = 0; attempt < maxAttempts; attempt++) {
        const response = http.get(url);

        if (response.status === 200) {
            try {
                const body = JSON.parse(response.body);
                if (body.status === 'COMPLETED' || body.status === 'PROCESSED') {
                    jobCompleted = true;
                    break;
                }
            } catch (err) {
                // Ignore parse errors on intermediate states
            }
        }

        sleep(pollIntervalSeconds);
    }

    check(jobCompleted, {
        'job reached terminal completed state before timeout': (status) => status === true,
    });
}