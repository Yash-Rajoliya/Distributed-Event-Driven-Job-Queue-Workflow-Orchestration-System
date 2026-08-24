import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    thresholds: {
        checks: ['rate==1.0'],
    },
};

export default function () {
    const jobId = `job-${__VU}-${__ITER}`;
    const url = `http://localhost:8080/jobs/status?jobId=${jobId}`;
    const maxAttempts = 10;
    const pollIntervalSeconds = 1;

    let jobCompleted = false;
    let terminalErrorEncountered = false;

    for (let attempt = 0; attempt < maxAttempts; attempt++) {
        const response = http.get(url, {
            headers: { 'Accept': 'application/json' },
            timeout: '3s',
        });

        if (response.status === 200 && response.body) {
            try {
                const body = JSON.parse(response.body);
                
                if (body.status === 'COMPLETED' || body.status === 'PROCESSED') {
                    jobCompleted = true;
                    break;
                }

                if (body.status === 'FAILED' || body.status === 'DEAD_LETTER') {
                    terminalErrorEncountered = true;
                    console.error(`Job ${jobId} failed prematurely on attempt ${attempt + 1}: ${body.errorReason || 'Unknown error'}`);
                    break;
                }
            } catch (err) {
                console.warn(`Attempt ${attempt + 1}: Failed to parse JSON response for job ${jobId}`);
            }
        }

        sleep(pollIntervalSeconds);
    }

    check(jobCompleted, {
        'job reached terminal completed state before timeout': (status) => status === true,
        'job did not encounter unrecoverable terminal failure': () => !terminalErrorEncountered,
    });
}