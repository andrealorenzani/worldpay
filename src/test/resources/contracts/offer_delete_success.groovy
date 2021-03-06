import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Add offer")
    request {
        method 'DELETE'
        url 'offer/so4/a'
        headers {
            header('Content-Type', 'application/json; charset=utf-8')
            header('Accept', 'application/json; charset=utf-8')
        }
    }

    response {
        status 200
        body(
                id: 'so4',
                title: 'Fourth Stored Offer',
                description: 'Description of the fourth stored offer',
                vendor: 'vendor1',
                price: 13000,
                currency: 'GBP',
                expiration: $(producer(regex("(3[01]|0[1-9]|[12][0-9])-(1[0-2]|0[1-9])-([0-9]{4}) (2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])")))

        )
        headers {
            header('Content-Type', 'application/json;charset=utf-8')
        }
    }
}
