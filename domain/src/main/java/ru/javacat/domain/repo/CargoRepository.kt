package ru.javacat.domain.repo

import ru.javacat.domain.models.CargoName

interface CargoRepository: BaseChooseItemRepository<CargoName, String, Long>