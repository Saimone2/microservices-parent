import http from 'k6/http';
import { check, group } from 'k6';
import { Trend } from 'k6/metrics';

let orderCreationResponseTime = new Trend('order_creation_response_time');

export let options = {
    vus: 5,
    duration: '60s',
    rps: 50
};

const uniqueProducts = [
    'a6a9074e-69cf-4f6c-a4d9-fc0ba8dc3247',
    'b55dc875-8df1-4373-b1da-4b8295c79ca1',
    '20e7e2e8-d1b7-471c-9007-94a6e86163f1',
    '54c812f7-7a40-44c7-8099-8340f528c9a4'
];

const userEmails = [
    'admin@gmail.com',
    'user1@gmail.com',
    'user2@gmail.com',
    'product_manager1@gmail.com',
    'product_manager2@gmail.com'
];

export default function () {
    const userEmail = userEmails[Math.floor(Math.random() * userEmails.length)];
    const randomStreetNumber = Math.floor(Math.random() * 100) + 1;
    const randomStreetName = ['Main', 'High', 'Park', 'Oak', 'Pine'][Math.floor(Math.random() * 5)];
    const deliveryAddress = `${randomStreetNumber} ${randomStreetName} St.`;

    const numUniqueProducts = Math.floor(Math.random() * 4) + 1;

    const selectedProducts = [];
    const usedIndices = new Set();
    while (selectedProducts.length < numUniqueProducts) {
        const index = Math.floor(Math.random() * 4);
        if (!usedIndices.has(index)) {
            usedIndices.add(index);
            selectedProducts.push(uniqueProducts[index]);
        }
    }

    const items = selectedProducts.map(productId => ({
        productId: productId,
        quantity: Math.floor(Math.random() * 10) + 1
    }));

    const payload = {
        deliveryAddress: deliveryAddress,
        items: items
    };

    group('Order creation scenario', function () {
        let res = http.post('http://order-service:8083/order/create', JSON.stringify(payload), {
            headers: {
                'Content-Type': 'application/json',
                'X-User-Email': userEmail
            }
        });

        orderCreationResponseTime.add(res.timings.duration);

        check(res, {
            'order creation status is 201': (r) => r.status === 201
        });

        console.log(`Response time: ${res.timings.duration} ms, Status: ${res.status}`);
    });
}