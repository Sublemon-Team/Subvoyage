#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;
#define NSCALE 124.0
#define DSCALE 125.0

#define NSCALE2 102.0
#define DSCALE2 95.0

#define step 2.0
#define step2 4.0
#define step3 7.0

void main() {
    vec2 c = v_texCoords;
    vec2 v = vec2(1.0 / u_resolution.x, 1.0 / u_resolution.y);

    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

    float stime = u_time * 0.1;
    vec4 noiseSample = texture2D(u_noise, coords / NSCALE2 + stime / 64.0);
    vec4 noiseSample2 = texture2D(u_noise, coords / DSCALE2 + stime / 80.0);

    vec2 offset = vec2(noiseSample.r, noiseSample2.r) * v * 1.5;

    vec4 orig = texture2D(u_texture, v_texCoords - offset);
    vec4 sampled = texture2D(u_texture, v_texCoords + offset);

    noiseSample = texture2D(u_noise, coords / DSCALE + stime / 512.0);
    noiseSample2 = texture2D(u_noise, coords / NSCALE + stime / 512.0);

    offset = vec2(noiseSample.r, noiseSample2.r) * v * 0.2;

    vec4 maxed = max(
    max(
    max(texture2D(u_texture, c + vec2(0.0, step) * v + offset), texture2D(u_texture, c + vec2(0.0, -step) * v + offset)),
    texture2D(u_texture, c + vec2(step, 0.0) * v + offset)
    ),
    texture2D(u_texture, c + vec2(-step, 0.0) * v + offset)
    );

    vec4 maxed2 = max(
    max(
    max(texture2D(u_texture, c + vec2(0.0, step2) * v + offset), texture2D(u_texture, c + vec2(0.0, -step2) * v + offset)),
    texture2D(u_texture, c + vec2(step2, 0.0) * v + offset)
    ),
    texture2D(u_texture, c + vec2(-step2, 0.0) * v + offset)
    );

    vec4 maxed3 = max(
    max(
    max(texture2D(u_texture, c + vec2(0.0, step3) * v + offset), texture2D(u_texture, c + vec2(0.0, -step3) * v + offset)),
    texture2D(u_texture, c + vec2(step3, 0.0) * v + offset)
    ),
    texture2D(u_texture, c + vec2(-step3, 0.0) * v + offset)
    );

    // Усиливаем цвет лазера (яркость и насыщенность)
    vec3 laserColor = orig.rgb * 2.0; // Делаем его более интенсивным
    float intensity = smoothstep(0.7, 1.0, max(sampled.a, orig.a)); // Усиливаем контраст

    // Центральное белое ядро
    vec3 core = vec3(1.0, 1.0, 1.0) * intensity * 1.5;

    gl_FragColor = vec4(core, max(sampled.a, orig.a));

    vec4 noiseSample3 = texture2D(u_noise, coords / vec2(128.0,116.0) * vec2(0.8) + stime / 64.0);
    float noi = noiseSample3.r;
    if(maxed3.a > 0.8 && (sampled.a < 0.8 && orig.a < 0.8) && noi < 0.5 && noi > 0.3)
    gl_FragColor = vec4(mix(maxed3.rgb, vec3(0.0), 0.1), 0.2f);

    if (maxed2.a > 0.8 && (sampled.a < 0.8 && orig.a < 0.8))
    gl_FragColor = vec4(mix(maxed2.rgb, vec3(1.0), 0.3), 1.0);

    if (maxed.a > 0.6 && (sampled.a < 0.6 && orig.a < 0.6))
    gl_FragColor = vec4(mix(maxed.rgb, vec3(1.0), 0.5), 1.0);
}
