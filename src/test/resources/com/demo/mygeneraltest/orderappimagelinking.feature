Feature: Purchase Order creation with a new product and an order app image
  Scenario: Purchase Order creation with a new product and an order app image
    When a purchase order "purchase-order-pim-new-product-with-image" is received from ordering app
    Then the asset "purchase-order-pim-new-product-with-image-linking" update call to Akeneo should be made
    And the taxonomy "purchase-order-pim-new-product-with-image-taxonomy" update call to Akeneo should be made

