import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Add offer")
    request {
        method 'DELETE'
        url 'offer/so3/a'
        headers {
            header('Content-Type', 'application/json; charset=utf-8')
            header('Accept', 'application/json; charset=utf-8')
        }
    }

    response {
        status 200
        body(
                id: 'so3',
                title: 'Third Stored Offer',
                description: 'Description of the third stored offer',
                vendor: 'vendor1',
                price: 3000,
                currency: 'GBP',
                expiration: $(producer(regex("(3[01]|0[1-9]|[12][0-9])-(1[0-2]|0[1-9])-([0-9]{4}) (2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])")))

        )
        headers {
            header('Content-Type', 'application/json;charset=utf-8')
        }
    }
}
