import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Add offer")
    request {
        method 'GET'
        url 'offer?description=first'
        headers {
            header('Content-Type', 'application/json; charset=utf-8')
            header('Accept', 'application/json; charset=utf-8')
        }
    }

    response {
        status 200
        body(
                offers:  [
                     id: 'so1',
                     title: 'First Stored Offer',
                     description: 'Description of the first stored offer',
                     vendor: 'vendor1',
                     price: 1000,
                     currency: 'GBP',
                     expiration: $(producer(regex("(3[01]|0[1-9]|[12][0-9])-(1[0-2]|0[1-9])-([0-9]{4}) (2[0-3]|[01][0-9]):([0-5][0-9]):([0-5][0-9])")))
                ]
        )
        headers {
            header('Content-Type', 'application/json;charset=utf-8')
        }
    }
}
