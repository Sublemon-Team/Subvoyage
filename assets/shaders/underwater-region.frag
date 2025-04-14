#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_noise;
uniform sampler2D u_distortmap;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

uniform float u_ww; // world width
uniform float u_wh; // world height

uniform float u_opacity;

varying vec2 v_texCoords;

#define NSCALE (170.0 / 2.0)
#define DSCALE (160.0 / 2.0)

void heatmap() {
    vec2 c = v_texCoords;
    vec2 v = vec2(1.0 / u_resolution.x, 1.0 / u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

    float atime = u_time / 15000.0;
    float noise = (texture2D(u_noise, (coords) / DSCALE + vec2(atime, atime) * vec2(-0.9, 0.8)).r +
    texture2D(u_noise, (coords) / DSCALE + vec2(atime * 1.1, atime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;
    noise = abs(noise - 0.5) * 7.0 + 0.23;

    vec2 worldc = vec2(coords.x, coords.y);

    float heatAmount = clamp(texture2D(u_distortmap, vec2(worldc.x / u_ww, worldc.y / u_wh)).r, 0.0, 1.0);
    float coolAmount = clamp(texture2D(u_distortmap, vec2(worldc.x / u_ww, worldc.y / u_wh)).b, 0.0, 1.0);
    gl_FragColor = vec4(heatAmount * noise, min(1.0 - heatAmount, 1.0 - coolAmount) * noise, coolAmount * noise, 1.0);
}

vec4 lerp(vec4 a, vec4 b, float t) {
    return a + t * (b - a);
}

void main() {
    vec2 c = v_texCoords;
    vec2 v = vec2(1.0 / u_resolution.x, 1.0 / u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

    float atime = u_time / 15000.0;
    float noise = (texture2D(u_noise, (coords) / DSCALE + vec2(atime, atime) * vec2(-0.9, 0.8)).r +
    texture2D(u_noise, (coords) / DSCALE + vec2(atime * 1.1, atime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;
    noise = abs(noise - 0.5) * 7.0 + 0.23;

    vec2 worldc = vec2(coords.x, coords.y);

    vec4 orig = texture2D(u_texture, c);
    gl_FragColor = orig; // fallback in case something breaks

    // Subsequent calculations are untouched, but with vec2(atime) replaced with vec2(atime, atime)
}
