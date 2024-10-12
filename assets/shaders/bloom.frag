#define HIGHP

#define S3 vec3(111.0, 129.0, 219.0) / 255.0
#define S2 vec3(90.0, 120.0, 179.0) / 255.0
#define S1 vec4(91.0, 105.0, 178.0, 255.0) / 255.0

#define NSCALE 180.0 / 2.0
#define DSCALE 130.0 / 2.0

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;

void main(){
    vec2 c = v_texCoords.xy;
    vec2 coords = (c * u_resolution) + u_campos;

    vec4 orig = texture2D(u_texture, c);

    float atime = u_time / 15000.0;
    float noise = (texture2D(u_noise, (coords) / DSCALE + vec2(atime) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / DSCALE + vec2(atime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;

    noise = abs(noise - 0.5) * 7.0 + 0.23;

    float btime = u_time / 9000.0;

    vec4 color = texture2D(u_texture, c);
    vec4 color1 = texture2D(u_texture, c+u_resolution);
    vec4 color2 = texture2D(u_texture, c-u_resolution);
    vec4 color3 = texture2D(u_texture, c+vec2(u_resolution.x,-u_resolution.y));
    vec4 color4 = texture2D(u_texture, c-vec2(u_resolution.x,-u_resolution.y));

    vec4 finalColor = (color1+color2+color3+color4)/3.0;

    gl_FragColor = finalColor;
}
