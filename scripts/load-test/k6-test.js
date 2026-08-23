import http from 'k6/http';

export default function () {
    http.post('http://localhost:8081/jobs', JSON.stringify({
        type: "EMAIL",
        payload: "test"
    }));
}