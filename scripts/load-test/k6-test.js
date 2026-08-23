import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 20 }, // Ramp up to 20 virtual users
        { duration: '1m', target: 50 },  // Sustained load with 50 concurrent users
        { duration: '30s', target: 0 },  // Ramp down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
        http_req_failed: ['rate<0.01'],   // Less than 1% failure rate under concurrency
    },
};

export default function () {
    const url = 'http://localhost:8081/jobs';
    
    // Generate distinct job identifiers to avoid duplicate key conflicts under concurrent execution
    const payload = JSON.stringify({
        jobId: `job-${__VU}-${__ITER}-${Date.now()}`,
        type: 'EMAIL',
        payload: 'test-content',
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(url, payload, params);

    check(res, {
        'status is 200 or 202': (r) => r.status === 200 || r.status === 202,
    });

    // Pacing pause to represent realistic user request pacing
    sleep(0.5);
}