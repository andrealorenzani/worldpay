import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Add offer")
    request {
        method 'GET'
        url 'offer?maxPrix=999'
        headers {
            header('Content-Type', 'application/json; charset=utf-8')
            header('Accept', 'application/json; charset=utf-8')
        }
    }

    response {
        status 200
        body(
                offers:  []
        )
        headers {
            header('Content-Type', 'application/json;charset=utf-8')
        }
    }
}
