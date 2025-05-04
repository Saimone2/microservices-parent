import http from 'k6/http';
import { sleep, check, group } from 'k6';
import { Trend } from 'k6/metrics';

 let registrationResponseTime = new Trend('registration_response_time');
 let loginResponseTime = new Trend('login_response_time');

export let options = {
    stages: [
        { duration: '15s', target: 10 },
        { duration: '30s', target: 100 },
        { duration: '15s', target: 0 }
    ]
};

export default function () {
    const randomEmail = `user_${Math.floor(Math.random() * 1000000000)}@gmail.com`;
    const randomPass = randomPassword(12);
    const randomFirstName = `User_${Math.floor(Math.random() * 1000)}`;
    const randomLastName = `Test_${Math.floor(Math.random() * 1000)}`;
    const randomPhone = `+38096${Math.floor(1000000 + Math.random() * 9000000)}`;

    function randomPassword(length) {
        const upperCase = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
        const lowerCase = 'abcdefghijklmnopqrstuvwxyz';
        const numbers = '0123456789';
        const allChars = upperCase + lowerCase + numbers;

        let password = '';
        password += upperCase.charAt(Math.floor(Math.random() * upperCase.length));
        password += numbers.charAt(Math.floor(Math.random() * numbers.length));

        for (let i = 2; i < length; i++) {
            password += allChars.charAt(Math.floor(Math.random() * allChars.length));
        }
        return password;
    }

    group('User registration scenario', function () {
        const payload = {
            email: randomEmail,
            password: randomPass,
            firstName: randomFirstName,
            lastName: randomLastName,
            phoneNumber: randomPhone,
            address: `${Math.floor(Math.random() * 100)} Kyivska st.`,
        };

        let res = http.post('http://api-gateway:8080/auth/register', JSON.stringify(payload), {
            headers: { 'Content-Type': 'application/json' },
        });

        registrationResponseTime.add(res.timings.duration);

        check(res, { 'registration status is 201': (r) => r.status === 201 });
        sleep(1);
    });

    group('User login scenario', function () {
        let loginRes = http.post('http://api-gateway:8080/auth/login', JSON.stringify({
            email: randomEmail,
            password: randomPass,
        }), { headers: { 'Content-Type': 'application/json' } });

        loginResponseTime.add(loginRes.timings.duration);

        check(loginRes, { 'login success': (r) => r.status === 200 });
    });
}