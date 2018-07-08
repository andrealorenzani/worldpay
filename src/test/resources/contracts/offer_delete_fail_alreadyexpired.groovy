import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Add offer")
    request {
        method 'DELETE'
        url 'offer/so5/a'
        headers {
            header('Content-Type', 'application/json; charset=utf-8')
            header('Accept', 'application/json; charset=utf-8')
        }
    }

    response {
        status 400
        body(
                    code: "02",
                    message: "Offer already expired"
        )
        headers {
            header('Content-Type', 'application/json;charset=utf-8')
        }
    }
}
