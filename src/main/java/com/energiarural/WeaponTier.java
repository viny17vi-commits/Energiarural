package com.energiarural;

/** As 5 armas de energia, da mais fraca/fácil até a mais forte/difícil. */
public enum WeaponTier {
    //            id                    dano base, dano extra, custo/golpe, capacidade, velocidade
    BASTAO_CHOQUE("bastao_choque", 2, 4, 50, 20_000, -2.4),
    ESPADA_VOLTAICA("espada_voltaica", 2, 7, 120, 50_000, -2.2),
    LAMINA_DE_ARCO("lamina_de_arco", 3, 10, 250, 100_000, -2.4),
    MARTELO_TESLA("martelo_tesla", 3, 15, 500, 250_000, -3.2),
    CEIFADORA_PLASMA("ceifadora_plasma", 4, 22, 1000, 500_000, -2.6);

    public final String id;
    public final double baseDamage;
    public final float extraDamage;
    public final int cost;
    public final int capacity;
    public final double speed;

    WeaponTier(String id, double baseDamage, float extraDamage, int cost, int capacity, double speed) {
        this.id = id;
        this.baseDamage = baseDamage;
        this.extraDamage = extraDamage;
        this.cost = cost;
        this.capacity = capacity;
        this.speed = speed;
    }
}
